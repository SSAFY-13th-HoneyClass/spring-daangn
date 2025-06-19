# Spring Security

## Spring Security이란

- spring기반 애플리케이션의 보안 프레임워크.
- 로그인.로그아웃/권한 체크/CSRF/인증.인가 한번에 담당.
- 모든 요청은 필터 체인부터 지나감.


## 작동 방식의 전체 흐름

> 요청 -> FilterChainProxy -> SecurityFilterChain -> 인증/인가 -> SecurityContext에 사용자 정보 저장


- `FilterChainProxy` : 모든 보안 필터 체인을 감싸는 필터
  - `List<SecurityFilterChain>` 을 멤버로 가짐
  - FilterChainProxy가 "요청 헤더/URL/HTTP메서드"를 보고 가장 조건이 맞는 SecurityFilterChain 하나만 선택해 실행
<br>

- `SecurityFilterChain` : 특정 요청에 적용되는 보안 필터 리스트
  - "이 요청이 내 차레인가?"를 판단하는 **RequestMather** + 통과되면 실행할 필터들 묶음
  - maches() 가 true이면 그 체인만 실행함

<br>

- `AuthenticationManager` : Authentication 토큰을 받아 "이 사용자 맞아?"(인증)를 결정함

- `UserDetailsService` : DB에서 사용자 정보를 가져오는 역할

- `SecurityContextHolder` : 현재 로그인된 사용자의 정보를 저장하는 장소

<br>
</br>

------------

<br>

## 자세한 흐름 : 클라이언트 -> 인증 완료 -> 사용자 정보 저장

```
브라우저 ▶ 톰캣(서블릿 컨테이너)
          │
          ├ [일반 필터 ①…]      ← 예: CharacterEncodingFilter, CORS
          ├ [FilterChainProxy]   ← Spring-Security 보안 필터 묶음
          ├ [일반 필터 ②…]      ← 예: HiddenHttpMethodFilter, 로깅
          └─────────────────── 모든 필터 통과
                 │
                 ▼
       DispatcherServlet  ← 스프링 MVC 진입점
```

### 1단계. 사용자가 요청을 보냄 (ex. 로그인 시도)

`[클라이언트] -> "/login" 요청 보냄 (username, password)`

- 이 요청이 Servlet Filter Chain으로 진입 -> 톰캣이 필터들을 차례로 호출함

<br>

### 2단계. FilterChainProxy: 요청에 맞는 Security Filter Chain 을 선택 

> FilterChainProxy가 URL 경로를 보고 "어떤 SecurityFilterChain을 적용할지" 결정

- RequestMatcher로 각 체인의 조건(/api/**, /login/**, /**) 등을 검사
- 첫번째로 일치하는 SecurityFilterChain 하나만 선택


1) JWT 체인 : 세션･CSRF 비활성 + Bearer 토큰 필터 활성 → 완전 STATELESS.
  
```
요청:  /api/**     (Authorization: Bearer … 헤더 O)              ←—  **JWT 전용 체인**  ──────────────────────────────────┐
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│ 1) SecurityContextHolderFilter        : 컨텍스트 로드/저장 (세션 X, 빈 컨텍스트)                                             │
│ 2) BearerTokenAuthenticationFilter    : JWT 서명·만료 검증  ➜  Authentication 주입                                          │
│ 3) CsrfFilter                         : **비활성**  (STATELESS) ➜ 토큰 방식이라 필요없음                                                        │
│ 4) Cors / HeaderWriterFilter …        : 부가 보호막 (필요 시)                                                               │
│ 5) FilterSecurityInterceptor          : 최종 권한(ROLE/SCOPE) 확인                                                         │
│ 6) ExceptionTranslationFilter         : 401 / 403 변환                                                                    │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
        │  통과 →  DispatcherServlet → Controller
        └  거부 →  AuthenticationEntryPoint(401) / AccessDeniedHandler(403)
```

2) 폼-로그인 체인: ID·PW 인증 + 세션 생성 + CSRF 보호.

```
요청:  POST /login   또는  /login/**                                  ←—  **폼-로그인/세션 체인**  ─────────────────────────────┐
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│ 1) SecurityContextHolderFilter        : 세션에서 컨텍스트 로드 / 저장                                                     │
│ 2) UsernamePasswordAuthenticationFilter: ID·PW 확인  ➜  Authentication 주입 + 세션 생성                                   │
│ 3) CsrfFilter                         : **활성**  (POST 보호)                                                           │
│ 4) Cors / HeaderWriterFilter …        : 부가 보호막 (필요 시)                                                               │
│ 5) FilterSecurityInterceptor          : 최종 권한(ROLE) 확인 (보통 permitAll)                                              │
│ 6) ExceptionTranslationFilter         : 401 / 403 변환                                                                    │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
        │  통과 →  DispatcherServlet → Controller  (로그인 성공 시 SuccessHandler로 리다이렉트)
        └  거부 →  AuthenticationEntryPoint(401) / AccessDeniedHandler(403)
```

3) 일반 세션 체인: 이미 로그인된 세션 활용, 권한만 재확인.


<br>
</br>

### 3단계. 로그인 요청을 누가 가로채고, 인증 토큰 어떻게 만들까

1) 폼-로그인(세션방식)

```
브라우저
  │ POST /login  (username, password)
  ▼
Tomcat ──▶ Servlet Filter Chain
  │        └─ UsernamePasswordAuthenticationFilter ───────────┐
  │            • 파라미터 username / password 추출             │
  │            • AuthenticationManager.authenticate() 호출     │
  │            • 성공 → Authentication 완성                    │
  │                                                          ▼
  │        SecurityContextHolderFilter     (세션에 컨텍스트 저장)
  ▼
DispatcherServlet
  │ 302 Redirect (SavedRequestAwareAuthenticationSuccessHandler)
  ▼
홈 컨트롤러 또는 원래 요청 URL
```

- 폼-로그인은 `UsernamePasswordAuthenticationFilter`가 자동으로 /login을 가로채서 처리
- 인증 후, `세션 + SecurityContext` 를 서버에 저장.
- CSRF 방어 필수 

2) JWT 방식

```
모바일 / SPA
  │ POST /api/auth/login  { "id": "...", "pw": "..." }
  ▼
Tomcat ──▶ Servlet Filter Chain
  │        (UsernamePasswordAuthenticationFilter는 비활성)
  ▼
AuthController
  │ id, pw → LoginService.login()
  ▼
AuthenticationManager.authenticate()
  │  성공 → Authentication 완성
  ▼
TokenProvider.generate()
  │  Access Token  (15 min)
  │  Refresh Token (7 days / Redis)
  ▼
HTTP 200 JSON
{
  "accessToken":  "...",
  "refreshToken": "..."
}
```

- JWT는 `UsernamePasswordAuthenticationFilter` 필터를 비활성화하고 **컨트롤러+서비스** 쪽에서 직접 인증 수행
  - `AuthController`  
- 세션을 만들지 않고 서명된 토큰을 클라이언트에게 돌려주고, 이후 요청마다 `Bearer 헤더`로 보냄
- 다음 과정은 밑에서 더 자세하게 .. 

<br>
</br>

### 4단계. AuthenticationManager → AuthenticationProvider 순차 호출

- `AuthenticationProvider` 는 구현한 사용자 인증 로직이 들어있음
- 이 provider는 내부에서 **UserDetailsService** 를 효출해서 사용자 정보를 DB 에서 꺼냄
- UserDetailsService 구현테에서 DB에서 유저를 찾고, `UserDetails` 객체를 반환

<br>
</br>

### 5단계. 인증 성공 -> Authentication 객체 생성 -> SecurityContext 에 인증정보 저장

- Authenrication 객체에는 로그인한 유저의 권한, 상태, 정보들이 담김
- 이 객체를 `SecurityContextHolder`에 저장
- SecurityContextHolder는 ThreadLocal을 사용해서 현재 요청 스레드에만 해당 유저 정보를 저장.
- 다음 요청 부터는 `SecurityContextHolderFilter`가 매번 요청 시작시에 로그인 정보 확인 -> 인증 유지


<br>
</br>

---------------

<br>
</br>


## 인증과 인가 흐름

1. 인증(Authentication) : 너 누구야? -> 로그인 같은거.

위의 흐름과 같음.

2. 인가(Authorization) : 너 이거 해도 돼? -> 권한(role)기준으로 판단 

```
[요청] → FilterChain
        → FilterSecurityInterceptor
            → AccessDecisionManager
                → AccessDecisionVoter
                    → 사용자 권한 ⊇ 필요한 권한? → YES/NO
→ YES → Controller
→ NO  → 403 Forbidden
```

- Spring Security의 마지막 필터 중 하나인 `FilterSecurityInterceptor`가 이요청을 처리.

  ### FilterSecurityInterceptor 의 동작 방식

  1)  현재 요청 URL에 필요한 권한 확인
  2)  현재 로그인한 사용자의 권한 목록 확인 (`Authentication.getAuthorities()`)
  3)  권한 있으면 통과, 없으면 예외 발생(`AccessDeniedException` → 403)

<br>

- `FilterSecurityInterceptor`가 `AccessDecisionManager` 에게 "얘 이거 해도 돼?" 물어봐
- AccessDecisionManager 는 내부적으로 `AccessDecisionVoter`를 가지고 있음.
- `AccessDecisionVoter` 가 ACCESS_GRANTED 외치면 통과 -> 다음 필터나 컨트롤러까지 보냄
  - 실패하면 AccessDeniedException 발생  

<br>
</br>

-------------------


## JWT를 이용한 인증 방식 자세하게

1. 로그인 시점 (id/pw -> JWT 발급)

```
[클라이언트] 로그인 요청
   ↓
Controller
   ↓
AuthenticationManager.authenticate()     ← 여기서 ID/PW 검증 (DB 조회)
   ↓
TokenProvider.generate() → JWT 발급 (AccessToken + RefreshToken 생성)
```

- 사용자가 아이디 + 비밀번호로 로그인할 때만 수행됨.
- DB 에 있는 유저 정보를 꺼내서 비밀번호 비교 후 인증 성공시 `Authentication` 객체를 생성

<br>

  ### AccessToken & RefreshToken

  1) AccessToken

  - API 요청을 인증할 때 사용하는 짧은 생명 주기의 토큰
  - 사용자 정보를 포함 (id, 권한, 만료시간)
  - 저장 위치(클라이언트): `localStorage` or `sessionStorage` or `HttpOnly Cookie`

  2) RefreshToken

  - AccessToken 이 만료되었을 때 **새 토큰을 받을 수 있는 권한** 을 가진 토큰
  - 일반적으로 사용자 ID만 담김
  - 저장 위치(클라이언트) : `localStorage` or `HttpOnly Cookie`
  - 저장 위치(서버) : **Redis**에 저장 하여 유효성 확인 + 강제 만료 가능하게

  ### 왜 RT(RefreshToken)는 클라이언트 + 서버 2군데에 저장해야할까?

  클라이언트 측 RT : AT가 만료될 때 "나 아직 로그인 중이니 새 AT 줘" 라고 서버에 제시하기 위해 필요.
  
  - AT 만료 -> 재발급 요청
    ```
    POST /api/auth/refresh
    Authorization: Bearer <refreshToken>
    ```

  서버 측 RT : "이 RT가 유효한지" "이미 폐기된 RT인지" 판단할 **실시간 근거**가 필요.

  > 화이트리스트 - 현재 서버가 신뢰하는 RT 모음
  > 블랙리스트 - 더 이상 허용하지 않을 Access/Refresh Token 모음 

  - 로그아웃 : 화이트리스트에서 RT를 즉시 삭제 & AT를 블랙리스트에 등록해 현재 세션 호툴 차단
  - 토큰 탈취 탐지 : 재발급 요청이 올 때마다 화이트리스트에 그 RT가 있는지 확인.
  - 동시 로그인 1회 제어 : 새 로그인 성공 시 새 RT를 화이트리스트에 set -> redis가 동일 key 를 자동 덮어씀 -> 이전 RT 무효
  - RT Rotation (1회용) : `/refresh` 올 때마다 `DEL old RT → SET new RT` 수행해서 RT 두번쓰면 에러나도록 함. 재사용 막기 위해서


2. 로그인 이후 요청 (JWT 인증)

```
[클라이언트] API 요청 (Authorization: Bearer <token>)
   ↓
Security Filter Chain 진입
   ↓
BearerTokenAuthenticationFilter
   → Authorization 헤더 파싱
   → JWT 서명, 만료 검증
   → SecurityContextHolder에 저장
```

- 이미 로그인해서 받은 JWT를 가지고 API 호출.
- JWT 자체가 유효한지만 검사.
- DB 조회 x
- AuthenticationManager도 호출되지 않음.

<br>

흐름 정리
```
[로그인 요청]
 → AuthController → 인증 성공 → AccessToken + RefreshToken 발급

[요청 시]
 → BearerTokenAuthenticationFilter → JWT 인증 → SecurityContext 저장

[AccessToken 만료]
 → /reissue 요청 → RefreshToken 검증 → AccessToken 재발급

[로그아웃]
 → AccessToken 블랙리스트 등록
 → 이후 요청 거부 처리
```

<br>
</br>


-------

## 세션 / 쿠키 / OAuth 차이 정리

### 1. 세션 기반 인증

- JSP 같은 전통적인 웹에서 사용
- `Stateful`
- 서버 메모리에 보통 저장 (redis도 가능)
- 로그인 시 서버에 세션 ID 생성, `Set-Cookie`로 브라우저에 전달.
- 클라이언트는 JSESSIONID 쿠키를 보냄
- 서버는 HttpSession에서 사용자 인증 정보를 유지

### 2. 쿠키 기반 

- REST API 에서 보통 사용 
- `Stateless` 
- 클라이언트에 토큰 형식으로 저장

### 3. OAuth2

- 위임 방식
- 인증 주체는 제 3자 (Google, kakao, Naver 등)
- 사용자 정보는 `OAuth2User`로 제공함
- 그 이후에는 JWT 발급이나 세션 저장 가능

```
1. 클라이언트 → /oauth2/authorization/google
2. 리다이렉트 → Google 로그인 화면
3. 로그인 성공 → redirect_uri + code
4. 서버 → 구글에 code 전달, access_token 요청
5. 서버 → 구글로부터 사용자 정보 조회
6. 로그인 처리 & 토큰 발급 (내 서버 기준)
```

1) `/oauth2/authorization/google` -> 내부적으로 구글의 인증 url로 리다이렉트 시킴

2) 사용자 -> 구글 로그인 창에서 로그인

  - 로그인하고 앱에 권한 허용하면,
  - 구글이 우리 서버의 redirect_url로 리다이렉트함

3) 구글 -> 우리 서버로 인가 코드 전달

   `GET https://yourserver.com/login/oauth2/code/google?code=abc123&state=xyz
`
  - 위의 예시는 구글이 준 임시 인가 코드인데,
  - 이 코드는 일회용이며 토큰으로 교환 가능

4) 서버 -> 구글에 code 전달하고 토큰 요청

  - 성공하면 AT을 발급

5) 서버 -> 구글 API로 사용자 정보 요청

  - 구글이 준 AT를 가지고 사용자 정보 요청
  - json 형태로 받으면 이 정보가 `OAuth2User` 객체로 변환됨.

6) 사용자 바탕으로 로그인 처리

   - Spring Security가 OAuth2UserService를 호출해서 사용자 정보를 처리함.
   - 여기서 DB에 사용자가 있나 없나 확인
   - 이때 JWT 를 발급함.


<br>
</br>

--------------

## 실습의 주요 클래스 및 역할

### `domain/User`
- 회원 엔티티
### `repository/UserRepository`
- 회원 데이터 조회 인터페이스 
### `service/CustomUserDetailsService`
- `UserDetailsService` 구현체
- DB에서 ID기반으로 `UserDetails` 반환  
### `jwt/TokenProvider`
- JWT 생성 / 파싱 / 검증 / Authentication 객체 복원
### `jwt/JwtAuthenticationFilter`
- 매 요청마다 실행되는 필터
- JWT 검증하고, SecurityContextHolder에 Authentication 저장
### `controller/AuthController`
- /signup: 회원가입
- /login: 로그인 후 JWT 반환
### `controller/UserController`
- /api/user/me: 인증된 사용자 정보 반환
### `config/SecurityConfig`
- Spring Security 필터 체인 설정
### `config/PasswordConfig`
- 비밀번호 암호화를 위한 BCryptPasswordEncoder Bean 등록

<br>

## 회원가입 및 로그인 API 구현 

회원가입
- URL: POST /api/auth/signup
![image](https://github.com/user-attachments/assets/e0bf1e5c-c2f9-41e8-a292-2c4a3deb071e)

로그인 (JWT 토큰 발급)
- URL: POST /api/auth/login
![image](https://github.com/user-attachments/assets/12f0d92a-5ff9-404d-a6ad-6392bda66472)

응답:
![image](https://github.com/user-attachments/assets/1f6e9824-a4d5-4435-aa0d-b8090d07a051)

## 토큰이 필요한 API 1개 이상 구현

사용자 정보 조회 API
- URL: GET /api/user/me
- Header : Authorization: Bearer <발급받은 JWT>
![image](https://github.com/user-attachments/assets/ab38a538-f569-45a1-976e-e6fdcd2b30c7)


### 요청에 토큰이 포함이 되지 않았다면?

![image](https://github.com/user-attachments/assets/484ae1a2-9fba-4f8e-badf-8cc55cf84fee)
- 서버는 해당요청을 인증되지 않은 것으로 판단, 접근 차단함. (`403 Forbidden`)
- 

<br>

### 전체 정리 
- `UserDetailsService`는 Spring Security에서 유저 정보 조회 시 사용되는 표준 인터페이스(처음에 내가 직접 구현해야하는 줄 앎)
- `CustomIserDetailsService`는 그 구현체.
  - DB에서 유저를 조회하여 `UserDetails` 반환  

- `TokenProvider`가 JWT와 관련된 모든 핵심 기능을 관리
- Security Filter Chain 에서 JWT 검증 후 인증 객체를 Spring Security Context에 등록함으로써 API 접근 통제함.

<br>
</br>


----------------------



## RT 발급 로직 구현하기

1. TokenProvider에 RT 발급 메서드 추가

```java
public String createRefreshToken(Long id) {
    Date now = new Date();
    Date expiration = new Date(now.getTime() + (1000L * 60 * 60 * 24 * 7)); // 7일

    return Jwts.builder()
            .setSubject(id.toString())
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
}
```

2. 로그인 할 떄 RT 도 발급 받음
```java
String refreshToken = tokenProvider.createRefreshToken(user.getId());
```

3. RT를 이용해 AT 재발급 API 구현 
```java
@PostMapping("/reissue")
    public Map<String, String> reissue(@RequestHeader("Authorization") String bearerToken) {

        // "Bearer " 제거
        String refreshToken = bearerToken.replace("Bearer ", "");

        // RT 유효성 검사
        if (!tokenProvider.validateAccessToken(refreshToken)) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // 유효성 검사
        String userId = tokenProvider.getTokenUserId(refreshToken);
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 새로운 AT 발급
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(), null, List.of(() -> user.getRole())
        );
        String newAccessToken = tokenProvider.createAccessToken(user.getId(), authentication);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        return response;
    }
```

![image](https://github.com/user-attachments/assets/ee92b911-ba9b-44d8-a4e8-b70095669cc5)

![image](https://github.com/user-attachments/assets/24821351-9733-40f8-9c7e-051774a78c6f)

<br>


### 발급한 RT은 어떻게 사용?

> 왜 사용하냐

- AT가 만료 될 때 사용자 재인증없이 새로운 AT를 사용하기 위해.
- 사용자가 로그인 상태를 유지하려면 토큰이 만료될 때마다 다시 로그인 해야해.
- 이때 RT를 사용하면 재로그인 없이 새로운 AT 발급할 수 있음.


> 여기서 내 궁금증.
> RT 발급말고, 애초에 AT를 길게 주면 안돼?

- 가능하긴 하지만, 보안적으로 위험하다.
- 탈취 시 피해입음. Access + Refresh 구조는 AT가 금방 만료되므로 피해 최소화
- JWT는 상태가 없기 때문에 실시간 제어안됨.
  - RT는 DB나 Redis에서 실시간 관리 가능
- 로그아웃 처리 불가능. (클라이언트가 AT를 들고 있기때문에 로그아웃해서 클라이언트가 처리해도 서버는 모름)

따라서,
- AT는 쉽게 노출될 위험이 있어 왜? -> 브라우저 저장소에 저장하거나 네트워크로 전송되니까
- 그래서 짧게 유지하고 RT로 백그라운드 자동 재로그인을 구현하는 것임.
