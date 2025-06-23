# spring-daangn

# 1. 당근마켓 ERD 다이어그램
<img width="788" alt="image" src="https://github.com/user-attachments/assets/8f358789-e71e-4ba3-b37f-cc63fb98910c" />

* 게시물 테이블
  * post_id: 고유아이디(PK)
  * user_id: 판매자아이디(FK)
  * neighborhood: 게시물 위치 주소 ID (FK → address_id)
  * status: 판매 상태 (판매중/예약중/판매완료 등)
  * price: 가격
  * title: 게시물 제목
  * description: 제품에 관한 설명
  * category_id: 카테고리
  * views: 조회수
  * *like_count: 좋아요수
  * chat_romm_count: 채팅방수

  -> 연관된 테이블
  - user_id → user.user_id (N:1, 판매자)
  - neighborhood → address.address_id (N:1)
  - post_id → like, chatroom 테이블에서 참조됨

* 회원 테이블
  * user_id: 고유아이디(PK)
  * id: 로그인 할 때 쓰는 회원아이디
  * password: 비밀번호
  * nickname: 닉네임
 
-> 연관된 테이블
  - address.user_id → 1:N (회원은 여러 주소를 가질 수 있음)
  - post.user_id → 1:N (회원은 여러 게시물을 등록 가능)
  - like.user_id → 1:N (회원은 여러 게시물을 좋아요 가능)
  - chatroom.seller_id / chatroom.buyer_id → 1:N (회원은 판매자/구매자로 여러 채팅방 가짐)
    
* 좋아요 테이블
  * post_id: 좋아요 받은 게시물 아이디(FK)
  * user_id: 좋아요 누른 회원아이디(FK)
 
-> 연관된 테이블(식별 관계)
  - user_id → user.user_id (N:1)
  - post_id → post.post_id (N:1)
  
* 채팅방 테이블
  * chatroom_id: 고유아이디(PK)
  * seller_id: 판메자아이디(FK)
  * buyer_id: 구매자아이디(FK)
  * post_id: 게시물아이디(FK)
 
-> 연관된 테이블
  - seller_id, buyer_id → user.user_id (각각 N:1)
  - post_id → post.post_id (N:1)
  - chatroom_id → chatmessage.chatroom_id에서 참조됨
    
* 채팅메세지 테이블
  * chatmessage_id: 고유아이디(PK)
  * chatroom_id: 소속 채팅방아이디(FK)
  * message: 메세지
  * isread: 읽음 여부
  * messagetype: 메세지 타입(enum타입: text, image 등)
  * time: 보낸 시간

-> 연관된 테이블
  - chatroom_id → chatroom.chatroom_id (N:1)
    
* 주소 테이블
  * address_id: 고유아이디(PK)
  * user_id: 회원아이디(FK)
  * neighborhood: 동네

-> 연관된 테이블
  - user_id → user.user_id (N:1 비식별 관계)


# 2. Repository 단위 테스트

* PostRepositoryTest

<img width="487" alt="image" src="https://github.com/user-attachments/assets/c7646f45-8c48-409d-9909-572da3488e45" />
<img width="481" alt="image" src="https://github.com/user-attachments/assets/d43cea89-950f-49a3-8d52-360fe2bdc941" />
<img width="719" alt="image" src="https://github.com/user-attachments/assets/823d07c8-7f6d-45ef-9436-2c572d60b250" />


* 실행결과

![image](https://github.com/user-attachments/assets/713b9b8b-cf62-42ec-a0f4-2d3f61fd430e)

![image](https://github.com/user-attachments/assets/4fddfc7f-0751-4338-9cd7-7825af0817bb)


# 3. N+1 문제 테스트

* N+1 문제 테스트

JPA에서 발생할 수 있는 N+1 문제**를 확인하고, fetch join을 통해 이를 해결하는 과정을 테스트.

* 테스트 목적

- 기본 findAll()호출 시 지연 로딩으로 인해 N+1 쿼리 발생 여부 확인
- fetch join을 활용해서 N+1 문제 해결 여부 확인

* 테스트 방식

- @BeforeEach로 5명의 사용자와 게시글 15개를 생성
- findAll()실행 후 getSeller().getNickname()호출 → N+1 발생 확인
- findAllWithSeller()실행 (fetch join 적용) → 1번의 쿼리로 해결 확인

* 실행 결과

- N+1 발생
<img width="654" alt="image" src="https://github.com/user-attachments/assets/25da804a-aeb0-47a4-bc56-f8bec570ad44" />
<img width="535" alt="image" src="https://github.com/user-attachments/assets/34f2c92b-fb84-4fc1-b5f6-9a3accad0c54" />

- N+1 해결

<img width="661" alt="image" src="https://github.com/user-attachments/assets/917aef8a-ddd4-4cd2-aabb-b71fda3578a0" />
<img width="535" alt="image" src="https://github.com/user-attachments/assets/32a619a0-1a7a-4491-bffc-1ab6d500ef5b" />
<img width="513" alt="image" src="https://github.com/user-attachments/assets/c64c511f-d6a9-408e-95ce-0941fe793c0b" />


# 5주차

## 1. JWT 인증(Authentication) 방법에 대해서 알아보기

## JWT

- 로그인 성공 시, 서버가 Access Token과 Refresh Token을 발급
- 클라이언트는 API 요청 시 Access Token을 헤더에 담아 전송
- 서버는 토큰의 서명(Signature)을 검증해 위조 여부 확인
- 토큰 자체에 사용자 정보가 담겨 있어 서버가 상태를 저장하지 않아도 됨

### jwt 구성

<img width="664" alt="image" src="https://github.com/user-attachments/assets/4d5946b4-e9d6-4ddb-9914-306ed1f8cdde" />

header

- 토큰의 타입과 해시 암호화알고리즘 구성

payload

- 실제 사용자 정보나 인증 관련 데이터가 담긴 **클레임(Claims)** 을 포함

- 클레임은 `key: value` 형식으로 여러개 넣을 수 있음.

- 종류: 등록 클레임, 공개 클레임, 비공개 클레임

signature

- 헤더, 페이로드, 시크릿키를 암호화 연산한 결과값

- 클라이언트가 페이로드를 변경하더라도 서버가 이 값을 통해 토큰이 위조 되어 있는지 검증

서버는 인증 요청이 들어오면

1. **JWT의 Header와 Payload를 디코딩**
2. **자기만 알고 있는 secret key로 Signature를 다시 계산**
3. **요청에 들어온 Signature와 비교**

이 과정을 통해 아래를 검증합니다:

- 이 토큰이 위조되지 않았는지
- 유효 기간이 지났는지
- 클레임(사용자 정보 등)이 조작되지 않았는지

### 전체흐름

1. 로그인 요청
- 클라이언트가 아이디, 비밀번호 등의 **자격 정보**를 서버로 전송
2. 토큰 발급
- 인증 성공 시 서버는 **Access Token**과 **Refresh Token**을 클라이언트에게 발급
    - Access Token: 유효기간 짧음, 인증용
    - Refresh Token: 유효기간 김, 재발급용
3. 요청 시 Access Token 사용
- 클라이언트는 이후 API 요청 시 `Authorization` 헤더에 **Access Token**을 담아 전송
    
    ```
    pgsql
    복사편집
    Authorization: Bearer <Access Token>
    
    ```
    
4. 서버에서 Access Token 검증
- 서버는 **Signature를 검증**하여 위조 여부와 유효성을 판단
5. 인증 성공 시 리소스 접근 허용
- 검증이 통과되면 **요청한 자원에 대한 접근이 허용**
6. Access Token 만료
- 일정 시간이 지나면 Access Token이 만료되고 서버는 거부 응답
7. Refresh Token을 통한 재발급 요청
- 클라이언트는 **만료된 Access Token + Refresh Token**을 서버에 전송하여 **새로운 Access Token 재발급 요청**
8. 서버에서 Refresh Token 검증 및 Access Token 재발급
- 서버는 Refresh Token이 유효한지 확인 후, 새로운 Access Token을 **재발급하여 반환**

### 장점

- 서버에 상태를 저장하지 않아도 되어 **확장성(Scale-out)에 유리**
- 토큰만으로 인증 가능하여 **성능이 뛰어남 (빠른 요청 처리)**
- 클라이언트가 인증 정보를 **직접 보관** → 서버 부담 적음
- **모바일/SPA(프론트 분리형)** 환경에 적합

### 단점

- **위조 위험** → Signature 검증 필요, HTTPS 필수
- **중간 무효화 어려움** (이미 발급된 토큰은 취소 불가)
- Payload가 노출될 수 있으므로 **민감 정보 저장 금지**
- 토큰 탈취 시 **보안 위험** (Refresh Token 관리 필요)

---

## **Cookie**

- **인증 정보 자체를 클라이언트 쿠키에 저장**하고,
- 서버는 쿠키에 담긴 값을 검증하여 인증 처리

### 전체흐름

1. 클라이언트가 로그인 요청
2. 서버가 사용자 인증 정보로 JWT나 사용자 ID 등을 암호화해 쿠키에 저장
3. 클라이언트는 쿠키를 브라우저에 저장
4. 이후 모든 요청에 쿠키가 함께 전송됨
5. 서버는 쿠키에 담긴 정보로 인증 여부를 판단

### 장점

- **클라이언트 중심 인증 방식**
- 서버 메모리 부담 없음

### 단점

- 쿠키 탈취 시 정보 유출 위험 (→ 암호화, HTTPS 필요)
- 쿠키 용량 제한(약 4KB)
- 쿠키 조작 가능성 (→ 서명/암호화 필요)

---

## Session

- 클라이언트가 로그인하면 서버는 사용자 정보를 **서버의 메모리(DB 등)에 세션으로 저장**하고,
- 해당 세션을 식별할 수 있는 **세션 ID(sessionId)**를 생성해 **쿠키에 담아 클라이언트에 전달**합니다.

### 전체 흐름

1. 클라이언트가 로그인 요청 (아이디/비밀번호 전송)
2. 서버가 인증 성공 시 세션 생성 → 사용자 정보 저장
3. 서버는 세션 ID를 응답 쿠키로 클라이언트에게 전달
4. 클라이언트는 이후 요청마다 쿠키에 담긴 세션 ID를 자동 전송
5. 서버는 세션 저장소에서 세션 ID로 사용자 정보 확인 후 처리

### 장점

- 서버가 인증 정보를 직접 관리 → **보안에 강함**
- 세션 무효화가 쉬움 (서버에서 세션 삭제)

### 단점

- 서버에 **세션 정보를 저장해야 하므로 부하 증가**
- 서버가 상태를 유지해야 하므로 **스케일 아웃에 불리**
- 분산 서버 구조에서는 세션 공유(세션 클러스터링)가 필요

---

## OAuth

- OAuth는 **제3자 애플리케이션**이 사용자의 **비밀번호를 알지 않고도**, 사용자의 **정보에 접근할 수 있도록 하는 권한 위임 방식**입니다.

> 예: 네이버 계정으로 우리 서비스 로그인 → 네이버는 비밀번호를 알려주지 않고, 우리 서비스에 "이 사용자가 네이버에 로그인한 것"을 인증만 해줌.
> 

### OAuth 인증 방식 흐름

1. **사용자**가 "구글로 로그인" 버튼 클릭
2. *클라이언트(우리 서비스)**는 **구글 OAuth 서버**에 인증 요청
3. 사용자는 구글 로그인 화면에서 **아이디/비밀번호** 입력하고 인증
4. 구글은 사용자에게 **동의 요청 화면**을 보여줌 (정보 제공 동의 등)
5. 사용자가 동의하면, **Authorization Code(인가 코드)** 발급
6. 클라이언트는 이 코드를 받아서, 구글에게 **Access Token 요청**
7. 구글은 **Access Token (및 선택적으로 Refresh Token)** 발급
8. 클라이언트는 Access Token을 가지고 구글 API를 호출해 사용자 정보를 가져옴 (ex. 이메일, 닉네임 등)
9. 가져온 정보를 바탕으로 우리 서비스에서 로그인 처리

### 장점

- **비밀번호 노출 없음**: 제3자에게 비밀번호를 넘기지 않음
- **토큰 기반**: Access Token 만료 시 Refresh Token으로 갱신 가능
- **소셜 로그인 연동**에 적합
- **범위(scope)** 설정으로 필요한 권한만 위임 가능

### 단점

- 구현 복잡도 높음 (OAuth 서버와의 통신 및 토큰 처리 필요)
- 보안 이슈: 토큰 탈취 시 정보 유출 위험 → HTTPS, 만료 시간 관리 필수
- **리디렉션 기반 흐름**이므로 모바일/앱에서 UX 구현이 어려울 수 있음


## 2. 액세스 토큰 발급 및 검증 로직 구현하기
### 액세스 토큰 발급
<img width="836" alt="image" src="https://github.com/user-attachments/assets/054fe0ad-45cb-4eb4-a573-c5fcba9f2c46" />

### 검증 로직
<img width="692" alt="image" src="https://github.com/user-attachments/assets/00a41aec-bde8-40ff-bc5d-5767ff8bce4a" />


## 3. 회원가입 및 로그인 API 구현하고 테스트하기
### 회원가입
<img width="1431" alt="image" src="https://github.com/user-attachments/assets/67c2a55d-008b-4e98-9ebc-1245bf226528" />
<img width="1410" alt="image" src="https://github.com/user-attachments/assets/a4945293-12eb-4aa0-833e-6361fb393d19" />

### 회원가입 성공
<img width="282" alt="image" src="https://github.com/user-attachments/assets/1e0bd829-6912-43e4-8b38-4c2c1cfc44c1" />

### 로그인
<img width="1422" alt="image" src="https://github.com/user-attachments/assets/b2a6db82-f3fe-47d6-8cae-7e0c979e92f2" />
<img width="1405" alt="image" src="https://github.com/user-attachments/assets/d28f4bbd-ba42-4997-93b8-ea30e0a54085" />

## 4. 토큰이 필요한 API 1개 이상 구현하고 테스트하기

### 토큰 없을 때 403 에러
<img width="1422" alt="image" src="https://github.com/user-attachments/assets/81d473ee-5dae-4c7d-bfd4-e642000392ed" />
<img width="1398" alt="image" src="https://github.com/user-attachments/assets/d0c89f4c-0f66-434d-a8b5-efd9a7b98552" />
<img width="886" alt="image" src="https://github.com/user-attachments/assets/709e8c9a-7569-4b17-b415-d65070201e04" />

결과가
403에러 발생....ㅠ


# 6주차

## 이미지 생성하고 실행하기

./gradlew bootJar 통해 build/libs/*.jar파일 생성 <br>
Dockerfile 생성 <br>
docker-compose.yml 생성 <br>
docker build -t {docker image 이름} {Dockerfile의 위치} -> 이미지 생성 <br>
docker run -p 8080:8080 {docker image 이름} -> 이미지 실행 <br>

![image](https://github.com/user-attachments/assets/8ad877a0-34ec-4a53-91cb-fb089adb65b3) <br>

## EC2 & RDS 인스턴스 만들기

<img width="1459" alt="image" src="https://github.com/user-attachments/assets/ea7756e4-a853-48df-bf29-0a59bf517ea7" />

<img width="1469" alt="image" src="https://github.com/user-attachments/assets/bfee7067-5130-4258-802b-9cf3bfc5dc25" />

## EC2에 도커 설치 

설치 후 도커 버전 확인

![image](https://github.com/user-attachments/assets/25a8e066-a0a4-40bc-b049-7d7225c9989c)

## EC2에서 도커로 이미지 실행

![image](https://github.com/user-attachments/assets/2a764f88-6010-4b1d-a138-d8064374699c)

## 배포 후 테스트하기

![image](https://github.com/user-attachments/assets/c837d8d7-b217-4f50-8cd7-601ffbdc618a)

![image](https://github.com/user-attachments/assets/a7608969-cc05-44b7-9be5-afb634263acc)

![image](https://github.com/user-attachments/assets/4a186765-f7c7-4d18-b1bd-3c55fb89532c)






