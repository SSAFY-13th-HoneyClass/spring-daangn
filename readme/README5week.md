# 1️⃣ JWT 인증(Authentication) 방법에 대해서 알아보기

## JWT (JSON Web Token)
- JSON 포맷을 사용하여 사용자 정보를 안전하게 표현하는 토큰
- 서버와 클라이언트 간 상태를 유지하지 않는 무상태(stateless) 인증 방식에 적합

## JWT 구성

- Header
```java
{
  "alg": "HS256",
  "typ": "JWT"
}
```
- Payload
```java
{
  "sub": "user123",
  "roles": ["ROLE_USER"],
  "iat": 1620000000,
  "exp": 1620003600
}
```
- Signature
    -   HMACSHA256(Base64UrlEncode(header) + "." + Base64UrlEncode(payload), secretKey)

## 엑세스 토큰 (Access Token)
- 정의 및 역할 : API 요청 시 본인 인증용으로 사용되는 짧은 수명 토큰
- 만료 시간 : 일반적으로 수 분에서 수 시간 단위로 짧게 설정
- 전송 방식 : HTTP Header

## 리프레시 토큰 (Refresh Token)
- 정의 및 역할 : 엑세스 토큰 만료시 새로운 엑세스 토큰을 발급받기 위해 사용
- 만료 시간 : 엑세스 토큰보다 긴 시간
- 저장 위치 : 서버 DB 또는 Redis에 저장하여 서버에서 토큰 유효성 관리
- 재발급 흐름 : 클라이언트가 리프레시 토큰을 전송하면 서버에서 검증 후 새 액세스 토큰 발급

# 세션, 쿠키, OAuth 방식 조사해보기

## 세션 기반 인증
- 설명 : 로그인 시 서버에 세션 정보를 저장하고, 클라이언트는 세션 ID를 쿠키에 저장하여 서버와 통신
- 장점 : 
  - 서버가 모든 세션 상태를 관리하므로 보안상 통제 용이
  - 만료 처리, 로그아웃 처리 용이

## 쿠키 기반 인증
- 설명 : 인증 정보를 쿠키에 저장하고 매 요청마다 자동으로 서버에 전송됨
- 장점 : 
  - HTTP 표준에 맞춰 자동 전송되어 편리 
  - XSS, CSRF 방지 설정 가능
- 단점 : 
  - 쿠키 탈취 시 위험
  - 클라이언트의 저장소에 의존

## OAuth 2.0
- 설명 : 제 3자 애플리케이션이 리소스 소유자를 대신하여 리소스에 접근할 수 있도록 허용하는 인증 프레임워크
- 주요 구성요소 : Resource Owner, Client, Authorization Server, Resource Server
- 장점 :
  - 제 3자 서비스 인증 가능 (Google, Kakao, Naver, Github 등)
  - 토큰 기반 인증 확장성 높음
- 단점 :
  - 구현 복잡도 높음
  - 잘못 구성하면 보안에 취약해질 수 있음

