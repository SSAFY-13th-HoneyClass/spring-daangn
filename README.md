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

## 2. 액세스 토큰 발급 및 검증 로직 구현하기
### 액세스 토근 발급
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



