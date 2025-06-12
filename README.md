# 지난주 보완 및 리펙토링

## 버전 이슈

swagger와 호환을 위해 spring boot 버전 다운그레이드 ( → )

# 구현 기능

## 회원 인증 관련 API 추가

| 메서드 | URI                 | 설명                                            |
| ------ | ------------------- | ----------------------------------------------- |
| `POST` | `/api/auth/signup`  | 회원가입                                        |
| `POST` | `/api/auth/login`   | 로그인 및 JWT 토큰 발급                         |
| `POST` | `/api/auth/refresh` | RefreshToken으로 AccessToken 재발급             |
| `POST` | `/api/auth/logout`  | 클라이언트 측 로그아웃 (RefreshToken 제거 안내) |

## JWT 기반 인증 시스템 구성

| 클래스                    | 역할                                      |
| ------------------------- | ----------------------------------------- |
| `AuthController`          | 인증 관련 API (회원가입/로그인/재발급 등) |
| `AuthService`             | 사용자 인증 및 토큰 발급 로직             |
| `JwtTokenProvider`        | JWT 생성, 검증, 이메일 추출 유틸          |
| `JwtAuthenticationFilter` | 요청에서 JWT를 추출하고 인증 처리         |
| `SecurityConfig`          | 필터 등록 및 인증 경로 설정               |

- `AccessToken` (Bearer Token, 1시간 유효)
- `RefreshToken` (Bearer Token, 2주 유효)
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

## 구현 중 이슈

### JWT 인증 토큰 입력했는데도 403 Forbidden 발생

문제 사항

- Swagger Authorize 버튼에 AccessToken을 입력했지만, Spring Security의 인증 필터가 동작하지 않음

문제 원인

- JwtAuthenticationFilter가 Spring Security FilterChain에 등록되어 있지 않어 UsernamePasswordAuthenticationFilter보다 뒤에 배치되어 인증 정보를 SecurityContext에 전달하지 못함

해결방안

- SecurityConfig에서 addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)로 JWT 필터를 명시적으로 등록

---

### ACCESS 토큰 재발급(/refresh) 시 Authorization 헤더가 null로 들어옴

문제 원인

- Swagger의 Authorize 버튼은 AccessToken용 Authorization 헤더만 자동 삽입.
- RefreshToken을 따로 보내야 하는 상황에서 @RequestHeader("Authorization")가 null로 처리됨.

해결방안

- Authorization 대신 X-Refresh-Token 같은 커스텀 헤더를 사용하여 Swagger에서 직접 입력하도록 변경

```java
@RequestHeader("X-Refresh-Token") String refreshToken
```

> [!NOTE]
>
> - JWT 필터는 반드시 Spring Security 필터 체인에 앞단에 배치
> - 모든 인증 실패/예외는 @ControllerAdvice를 통한 글로벌 예외 처리로 관리 권장
