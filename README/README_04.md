# 지난주 보완 및 리펙토링

## 버전 이슈

swagger와 호환을 위해 spring boot 버전 다운그레이드 (3.4.5 → 3.1.6)

# 구현 기능

## 의존성 추가

```
// Spring Security
implementation 'org.springframework.boot:spring-boot-starter-security'

// JWT 토큰 생성을 위한 jjwt
implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
```

## 회원 인증 관련 API 추가

![image](https://github.com/user-attachments/assets/d080b1e6-2c68-4afd-a13c-b6a4a5235653)

| 메서드 | URI                 | 설명                                            |
| ------ | ------------------- | ----------------------------------------------- |
| `POST` | `/api/auth/signup`  | 회원가입                                        |
| `POST` | `/api/auth/login`   | 로그인 및 JWT 토큰 발급                         |
| `POST` | `/api/auth/refresh` | RefreshToken으로 AccessToken 재발급             |
| `POST` | `/api/auth/logout`  | 클라이언트 측 로그아웃 (RefreshToken 제거 안내) |

> [!NOTE]
>
> - 인증 API는 글로벌한 엔트리포인트로 취급
> - 프론트엔드, 외부 서비스에서 일관된 경로로 접근이 필요
> - 인증 API는 다른 기능(API v1, v2 등)과 비교해 상대적으로 변경이 적고, 버전 관리의 필요성이 낮음
> - 따라서 버전을 포함하지 않고 고정된 URI(`/api/auth/...`) 사용!

## JWT 기반 인증 시스템 구성

| 클래스                    | 역할                                      |
| ------------------------- | ----------------------------------------- |
| `AuthController`          | 인증 관련 API (회원가입/로그인/재발급 등) |
| `AuthService`             | 사용자 인증 및 토큰 발급 로직             |
| `JwtTokenProvider`        | JWT 생성, 검증, 이메일 추출 유틸          |
| `JwtAuthenticationFilter` | 요청에서 JWT를 추출하고 인증 처리         |
| `SecurityConfig`          | 필터 등록 및 인증 경로 설정               |

- `AccessToken` (Bearer Token, 1시간 유효 : 3600000)
- `RefreshToken` (Bearer Token, 2주 유효 : 1209600000)
- `Authorization` 헤더 → AccessToken 전달
- `X-Refresh-Token` 헤더 → RefreshToken 전달

## 처리 흐름

### 회원가입 시

1. 이메일 중복 검사
2. 비밀번호 BCrypt 해싱 후 저장
3. 성공 시 MemberResponseDto 반환

### 로그인 시

1. 이메일 존재 여부 확인
2. 비밀번호 일치 여부 검증
3. JWT Access/RefreshToken 발급 및 응답

## Swagger 연동

Swagger 테스트 방법

- AccessToken은 Authorize 버튼
- RefreshToken은 개별 헤더 입력

### 회원가입

![image](https://github.com/user-attachments/assets/9c6a4d50-24b3-423f-8b36-a55e35a40fee)

### 로그인

![image](https://github.com/user-attachments/assets/b060ee48-f33e-416a-a560-b27d41ad4a20)

- ACCESS TOKEN과 REFRESH TOKEN 발급

### REFRESH TOKEN을 통한 ACCESS TOKEN 재발급

![image](https://github.com/user-attachments/assets/825a9af3-90ca-46a0-8978-4fd7f31e737c)

### ACCESS TOKEN으로 인증 전(403 에러 발생)

![image](https://github.com/user-attachments/assets/3658b5f2-c55a-4786-b95b-97d034a30c67)

### ACCESS TOKEN으로 인증

![image](https://github.com/user-attachments/assets/333f09c3-747d-417a-83cc-25b5faa7d5e2)

### ACCESS TOKEN으로 인증 후

![image](https://github.com/user-attachments/assets/38a17a26-fd7e-4d0f-9b9a-2aec52b51953)

# 구현 중 이슈

## JWT 인증 토큰 입력했는데도 403 Forbidden 발생

문제 사항

- Swagger UI에서 로그인 후 반환된 accessToken을 "Authorize" 창에 Bearer {accessToken} 형식으로 입력
- 이후 API 요청을 보냈는데도 403 Forbidden이 응답

문제 원인

- JWT 토큰이 필터에서 처리가 안됨!
  - Security 필터 체인에 JwtAuthenticationFilter가 등록되지 않았거나, UsernamePasswordAuthenticationFilter보다 앞서서 작동하지 않음
  - JwtAuthenticationFilter가 실행되지 않으면 SecurityContext에 인증 정보가 설정되지 않음 → 권한 없음(403)
- 확인 결과
  - JwtAuthenticationFilter가 Spring Security FilterChain에 등록되어 있지 않아 UsernamePasswordAuthenticationFilter보다 뒤에 배치되어 인증 정보를 SecurityContext에 전달하지 못함

해결방안

- SecurityConfig에서 addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)로 JWT 필터를 명시적으로 등록

```java
@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // H2 콘솔 iframe 허용
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
```

> [!NOTE]
> JWT 필터는 반드시 Spring Security 필터 체인에 앞단에 배치

## ACCESS 토큰 재발급(/refresh) 시 Authorization 헤더가 null로 들어옴

문제 사항

- @RequestHeader("Authorization") String authorizationHeader로 값을 받으려 했으나, 콘솔 로그로 `authorizationHeader : null`
- NullPointerException 발생

문제 원인

- Swagger의 Authorize 버튼은 AccessToken만 적용
  - Swagger UI에서 "Authorize"에 Bearer <accessToken>을 넣어도, Swagger에서 전역 Authorization 헤더로 적용되며 refresh API에는 AccessToken이 전달됨
  - 하지만 refresh API는 Authorization 헤더로 RefreshToken을 받아야 하는 구조이므로 헤더 충돌 또는 누락 발생.
- Spring의 @RequestHeader("Authorization")는 해당 헤더가 명시적으로 요청에 포함돼야 동작
  - @RequestHeader("Authorization")은 Swagger에서 테스트할 때, Try it out → Headers 부분에 직접 명시하지 않으면 전달되지 않음

해결방안

- 헤더 이름을 변경해 충돌 방지
- Authorization 대신 X-Refresh-Token 커스텀 헤더를 사용하여 Swagger에서 직접 입력하도록 변경

```java
@PostMapping("/refresh")
public ResponseEntity<ApiResponseDto<LoginResponseDto>> refresh(
        @RequestHeader("X-Refresh-Token") String refreshToken) {
    LoginResponseDto tokens = authService.refreshAccessToken(refreshToken);
    return ResponseEntity.ok(ApiResponseDto.success(tokens));
}
```

> [!NOTE]
> 모든 인증 실패/예외는 @ControllerAdvice를 통한 글로벌 예외 처리로 관리 권장
