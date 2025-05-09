# spring-daangn
1. 당근마켓 ERD 다이어그램
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
 
-> 연관된 테이블
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




2. Repository 단위 테스트

* PostRepositoryTest

<img width="487" alt="image" src="https://github.com/user-attachments/assets/c7646f45-8c48-409d-9909-572da3488e45" />
<img width="481" alt="image" src="https://github.com/user-attachments/assets/d43cea89-950f-49a3-8d52-360fe2bdc941" />
<img width="719" alt="image" src="https://github.com/user-attachments/assets/823d07c8-7f6d-45ef-9436-2c572d60b250" />



* 실행결과

![image](https://github.com/user-attachments/assets/713b9b8b-cf62-42ec-a0f4-2d3f61fd430e)

![image](https://github.com/user-attachments/assets/4fddfc7f-0751-4338-9cd7-7825af0817bb)


