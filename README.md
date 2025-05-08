# 당근마켓 클론 프로젝트

## 서비스 소개

- 당근마켓 클론 프로젝트
- 사용자가 물품을 사진과 함께 자유롭게 거래글을 게시
- 게시글에는 댓글, 대댓글, 좋아요 기능
- 사용자간 1:1 다이렉트 메시지를 통한 거래

### 주요 기능

- 로그인/회원가입
- 게시글 조회/등록/검색/삭제
- 사진 첨부
- 게시글, 댓글(대댓글), 좋아요 조회/등록/검색/삭제
- 1:1 다이렉트 메시지
- 지역 분류

> 현재 모델링에는 로그인/회원가입과 같은 사용자 관련 기능과, 게시글/댓글(대댓글)/좋아요/다이렉트 메시지에 대해서만 구현하였다.
> 지역 관련 가능이나 세부적인 검색 기능에 대해서는 미구현하였다.

---

## 데이터 모델링

### ERD 다이어그램

**ERDCloud**
![image](https://github.com/user-attachments/assets/45ed0e9e-5642-4b91-b673-15f2f05f8cb3)

**Mermaid**

```mermaid
erDiagram
    USERS {
        BIGINT user_id PK "회원 고유 ID"
        VARCHAR username "닉네임"
        VARCHAR email "로그인용 이메일"
        VARCHAR password "비밀번호"
        VARCHAR profile_url "프로필 사진 URL"
        DATETIME created_at "생성일시"
        DATETIME updated_at "수정일시"
        BOOLEAN is_deleted "탈퇴 여부"
    }

    POSTS {
        BIGINT post_id PK "게시글 고유 ID"
        BIGINT user_id FK "작성자"
        VARCHAR title "글 제목"
        TEXT content "본문 내용"
        DATETIME created_at "생성일시"
        DATETIME updated_at "수정일시"
        BOOLEAN is_deleted "삭제 여부"
    }

    POST_PHOTOS {
        BIGINT photo_id PK "사진 고유 ID"
        BIGINT post_id FK "대상 게시글"
        VARCHAR url "이미지 URL"
        INT order "정렬 순서"
    }

    COMMENTS {
        BIGINT comment_id PK "댓글 고유 ID"
        BIGINT post_id FK "대상 게시글"
        BIGINT user_id FK "작성자"
        BIGINT parent_comment_id FK "상위 댓글 ID"
        TEXT content "댓글 내용"
        DATETIME created_at "생성일시"
        DATETIME updated_at "수정일시"
        BOOLEAN is_deleted "삭제 여부"
    }

    POST_LIKES {
        BIGINT user_id FK "좋아요 누른 사용자"
        BIGINT post_id FK "좋아요 대상 게시글"
        DATETIME created_at "좋아요 표시일시"
    }

    DIRECT_MESSAGES {
        BIGINT message_id PK "메시지 고유 ID"
        BIGINT user_id FK "보낸 사람"
        BIGINT user_id2 FK "받는 사람"
        TEXT content "메시지 본문"
        DATETIME created_at "전송일시"
        BOOLEAN is_read "읽음 여부"
        BOOLEAN is_deleted "삭제 여부"
    }

    USERS ||--o{ POSTS          : 작성
    POSTS ||--o{ POST_PHOTOS    : 첨부
    POSTS ||--o{ COMMENTS       : 댓글
    USERS ||--o{ COMMENTS       : 작성
    COMMENTS ||--o{ COMMENTS     : 대댓글
    USERS ||--o{ POST_LIKES     : 좋아요
    POSTS ||--o{ POST_LIKES     : 좋아요
    USERS ||--o{ DIRECT_MESSAGES: 보낸메시지
    USERS ||--o{ DIRECT_MESSAGES: 받은메시지
```
> [!NOTE]
> 그동안 ERD는 Mermaid를 통해서만 작성해왔는데 이번에 처음으로 ERDCloud를 사용하면서 훨씬더 직관적으로 표현할 수 있다는 점이 좋았다.

### 스키마

| 테이블명        | 설명                  |
| --------------- | --------------------- |
| users           | 서비스 사용자 정보      |
| posts           | 거래 게시글             |
| post_photos     | 게시글에 첨부된 사진    |
| comments        | 게시글 댓글 및 대댓글   |
| post_likes      | 게시글 좋아요           |
| direct_messages | 1:1 다이렉트 메시지(DM) |

### users

| 컬럼        | 타입         | 제약조건                     | 기본값            | 설명            |
| ----------- | ------------ | ---------------------------- | ----------------- | --------------- |
| user_id     | BIGINT       | PK, NOT NULL, AUTO_INCREMENT |                   | 회원 고유 ID    |
| username    | VARCHAR(50)  | NOT NULL                     |                   | 닉네임          |
| email       | VARCHAR(100) | NOT NULL, UNIQUE             |                   | 로그인용 이메일 |
| password    | VARCHAR(255) | NOT NULL                     |                   | 비밀번호(해시)  |
| profile_url | VARCHAR(255) | NULL                         |                   | 프로필 사진 URL |
| created_at  | DATETIME     | NOT NULL                     | CURRENT_TIMESTAMP | 생성일시        |
| updated_at  | DATETIME     | NOT NULL                     | CURRENT_TIMESTAMP | 수정일시        |
| is_deleted  | BOOLEAN      | NOT NULL                     | FALSE             | 탈퇴 여부       |

### posts

| 컬럼       | 타입         | 제약조건                      | 기본값            | 설명           |
| ---------- | ------------ | ----------------------------- | ----------------- | -------------- |
| post_id    | BIGINT       | PK, NOT NULL, AUTO_INCREMENT  | —                 | 게시글 고유 ID |
| user_id    | BIGINT       | NOT NULL, FK → users(user_id) | —                 | 작성자         |
| title      | VARCHAR(200) | NOT NULL                      | —                 | 글 제목        |
| content    | TEXT         | NOT NULL                      | —                 | 본문 내용      |
| created_at | DATETIME     | NOT NULL                      | CURRENT_TIMESTAMP | 생성일시       |
| updated_at | DATETIME     | NOT NULL                      | CURRENT_TIMESTAMP | 수정일시       |
| is_deleted | BOOLEAN      | NOT NULL                      | FALSE             | 삭제 여부      |

### post_photos

| 컬럼     | 타입         | 제약조건                      | 기본값 | 설명         |
| -------- | ------------ | ----------------------------- | ------ | ------------ |
| photo_id | BIGINT       | PK, NOT NULL, AUTO_INCREMENT  |        | 사진 고유 ID |
| post_id  | BIGINT       | NOT NULL, FK → posts(post_id) |        | 대상 게시글  |
| url      | VARCHAR(500) | NOT NULL                      |        | 이미지 URL   |
| order    | INT          | NOT NULL                      |        | 정렬 순서    |

### comments

| 컬럼              | 타입     | 제약조건                        | 기본값            | 설명         |
| ----------------- | -------- | ------------------------------- | ----------------- | ------------ |
| comment_id        | BIGINT   | PK, NOT NULL, AUTO_INCREMENT    |                   | 댓글 고유 ID |
| post_id           | BIGINT   | NOT NULL, FK → posts(post_id)   |                   | 대상 게시글  |
| user_id           | BIGINT   | NOT NULL, FK → users(user_id)   |                   | 작성자       |
| parent_comment_id | BIGINT   | NULL, FK → comments(comment_id) | NULL              | 상위 댓글 ID |
| content           | TEXT     | NOT NULL                        |                   | 댓글 내용    |
| created_at        | DATETIME | NOT NULL                        | CURRENT_TIMESTAMP | 생성일시     |
| updated_at        | DATETIME | NOT NULL                        | CURRENT_TIMESTAMP | 수정일시     |
| is_deleted        | BOOLEAN  | NOT NULL                        | FALSE             | 삭제 여부    |

### post_likes

| 컬럼       | 타입     | 제약조건                      | 기본값            | 설명               |
| ---------- | -------- | ----------------------------- | ----------------- | ------------------ |
| user_id    | BIGINT   | NOT NULL, FK → users(user_id) |                   | 좋아요 누른 사용자 |
| post_id    | BIGINT   | NOT NULL, FK → posts(post_id) |                   | 좋아요 대상 게시글 |
| created_at | DATETIME | NOT NULL                      | CURRENT_TIMESTAMP | 좋아요 표시일시    |

### direct_messages

| 컬럼       | 타입     | 제약조건                      | 기본값            | 설명           |
| ---------- | -------- | ----------------------------- | ----------------- | -------------- |
| message_id | BIGINT   | PK, NOT NULL, AUTO_INCREMENT  |                   | 메시지 고유 ID |
| user_id    | BIGINT   | NOT NULL, FK → users(user_id) |                   | 보낸 사람      |
| user_id2   | BIGINT   | NOT NULL, FK → users(user_id) |                   | 받은 사람      |
| content    | TEXT     | NOT NULL                      |                   | 메시지 본문    |
| created_at | DATETIME | NOT NULL                      | CURRENT_TIMESTAMP | 전송일시       |
| is_read    | BOOLEAN  | NOT NULL                      | FALSE             | 읽음 여부      |
| is_deleted | BOOLEAN  | NOT NULL                      | FALSE             | 삭제 여부      |

### 관계

```
users 1:N posts
posts 1:N post_photos
posts 1:N comments
comments 1:N comments (대댓글은 자기참조)
users 1:N comments
users N:M posts via post_likes
users 1:N direct_messages
```

> [!IMPORTANT]
> 테이블에 is_deleted를 붙이는 이유
>
> 1. 데이터 복구
>
>     - 물리적으로 삭제(purge)할 경우 삭제된 레코드를 복구하기 어렵다.
>     - is_deleted 값을 true로만 변경하면, 실수로 삭제했을 때 곧바로 다시 활성화
>
> 2. 이력 관리
>
>     - 누가 언제 삭제했는지 로그를 남겨서 보안 문제시 추적 가능
>     - 삭제 전후의 상태를 비교 가능
>
> 3. 참조 무결성(Referential Integrity) 유지
>
>     - 외래키 제약조건이 걸린 테이블에서 물리 삭제를 하면 관계가 끊어져 오류가 발생할 수 있다.
>
> 4. 급격한 데이터 손실 방지
>
>     - 의도치 않은 배치작업, 버그, 또는 잘못된 조건절로 인해 다량의 레코드가 삭제되는 사고를 막아준다.
>
> 5. 운영 중 다운타임 최소화
>
>     - 대용량 테이블에서 물리 삭제를 바로 수행하면 잠금(lock)이나 블로킹(blocking) 유발 가능

---

## Repository 단위 테스트

### 테스트 대상

ForeignKey( `Comment.post` / `Comment.user` )를 포함하는 `Comment` 엔티티에 대해 Repository가 정상 동작하는지 검증하였다.

### 테스트 과정

CRUD 기능 검증

- Given : 새로운 User와 Post 엔티티 저장, Comment 엔티티 3개를 생성 및 저장
- When : CommentRepository.findAll() 호출
- Then : 저장된 3개의 Comment 모두 조회

### 테스트 코드

로깅 레벨, 포맷 설정

`application.properties`

```
# SQL 자체 출력
spring.jpa.show-sql=true
# 잘 포맷된 SQL 출력
spring.jpa.properties.hibernate.format_sql=true
# Hibernate SQL 로그 레벨 DEBUG
logging.level.org.hibernate.SQL=DEBUG
# 파라미터 바인딩 정보 출력 (바인딩 값)
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

`CommentRepositoryTest.java`

```java
// given
User user = new User("tester", "tester@example.com", "pwd", null);
user = userRepository.save(user);

Post post = new Post(user, "Test Post", "Content");
post = postRepository.save(post);

Comment c1 = new Comment(post, user, null, "First comment");
Comment c2 = new Comment(post, user, null, "Second comment");
Comment c3 = new Comment(post, user, null, "Third comment");

commentRepository.saveAll(List.of(c1, c2, c3));

// when
List<Comment> result = commentRepository.findAll();

// then
assertThat(result).hasSize(3)
    .extracting(Comment::getContent)
    .containsExactlyInAnyOrder(
        "First comment",
        "Second comment",
        "Third comment"
    );
```

> [!NOTE]
> 슬라이스 테스트
> - @DataJpaTest(+ H2) 사용
> - JPA Repository만 단독 검증
> - JPA 쿼리와 CRUD 테스트에 특화, 빠른 테스트 속도
> - 서비스/컨트롤러는 테스트 불가
> - 아래 자세히 설명


### 테스트 결과

디버그 콘솔에서 확인 로그 가능

```
    create table comments (
        comment_id bigint generated by default as identity,
        content TEXT not null,
        created_at timestamp(6) not null,
        is_deleted boolean not null,
        updated_at timestamp(6) not null,
        parent_comment_id bigint,
        post_id bigint not null,
        user_id bigint not null,
        primary key (comment_id)
    )
Hibernate:
    create table comments (
        comment_id bigint generated by default as identity,
        content TEXT not null,
        created_at timestamp(6) not null,
        is_deleted boolean not null,
        updated_at timestamp(6) not null,
        parent_comment_id bigint,
        post_id bigint not null,
        user_id bigint not null,
        primary key (comment_id)
    )
2025-05-09T02:16:50.104+09:00 DEBUG 26956 --- [spring-daangn] [           main] org.hibernate.SQL                        :
    create table posts (
        post_id bigint generated by default as identity,
        content TEXT not null,
        created_at timestamp(6) not null,
        is_deleted boolean not null,
        title varchar(200) not null,
        updated_at timestamp(6) not null,
        user_id bigint not null,
        primary key (post_id)
    )
Hibernate:
    create table posts (
        post_id bigint generated by default as identity,
        content TEXT not null,
        created_at timestamp(6) not null,
        is_deleted boolean not null,
        title varchar(200) not null,
        updated_at timestamp(6) not null,
        user_id bigint not null,
        primary key (post_id)
    )
2025-05-09T02:16:50.105+09:00 DEBUG 26956 --- [spring-daangn] [           main] org.hibernate.SQL                        :
    create table users (
        user_id bigint generated by default as identity,
        created_at timestamp(6) not null,
        email varchar(100) not null,
        is_deleted boolean not null,
        password varchar(255) not null,
        profile_url varchar(255),
        updated_at timestamp(6) not null,
        username varchar(50) not null,
        primary key (user_id)
    )
Hibernate:
    create table users (
        user_id bigint generated by default as identity,
        created_at timestamp(6) not null,
        email varchar(100) not null,
        is_deleted boolean not null,
        password varchar(255) not null,
        profile_url varchar(255),
        updated_at timestamp(6) not null,
        username varchar(50) not null,
        primary key (user_id)
    )
2025-05-09T02:16:50.106+09:00 DEBUG 26956 --- [spring-daangn] [           main] org.hibernate.SQL                        :
    alter table if exists users
       drop constraint if exists UK6dotkott2kjsp8vw4d0m25fb7
Hibernate:
    alter table if exists users
       drop constraint if exists UK6dotkott2kjsp8vw4d0m25fb7
2025-05-09T02:16:50.107+09:00 DEBUG 26956 --- [spring-daangn] [           main] org.hibernate.SQL                        :
    alter table if exists users
       add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email)
Hibernate:
    alter table if exists users
       add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email)
2025-05-09T02:16:50.109+09:00 DEBUG 26956 --- [spring-daangn] [           main] org.hibernate.SQL                        :
    alter table if exists comments
       add constraint FK7h839m3lkvhbyv3bcdv7sm4fj
       foreign key (parent_comment_id)
       references comments
Hibernate:
    alter table if exists comments
       add constraint FK7h839m3lkvhbyv3bcdv7sm4fj
       foreign key (parent_comment_id)
       references comments
2025-05-09T02:16:50.114+09:00 DEBUG 26956 --- [spring-daangn] [           main] org.hibernate.SQL                        :
    alter table if exists comments
       add constraint FKh4c7lvsc298whoyd4w9ta25cr
       foreign key (post_id)
       references posts
Hibernate:
    alter table if exists comments
       add constraint FKh4c7lvsc298whoyd4w9ta25cr
       foreign key (post_id)
       references posts
2025-05-09T02:16:50.115+09:00 DEBUG 26956 --- [spring-daangn] [           main] org.hibernate.SQL                        :
    alter table if exists comments
       add constraint FK8omq0tc18jd43bu5tjh6jvraq
       foreign key (user_id)
       references users
Hibernate:
    alter table if exists comments
       add constraint FK8omq0tc18jd43bu5tjh6jvraq
       foreign key (user_id)
       references users
2025-05-09T02:16:50.116+09:00 DEBUG 26956 --- [spring-daangn] [           main] org.hibernate.SQL                        :
    alter table if exists posts
       add constraint FK5lidm6cqbc7u4xhqpxm898qme
       foreign key (user_id)
       references users
Hibernate:
    alter table if exists posts
       add constraint FK5lidm6cqbc7u4xhqpxm898qme
       foreign key (user_id)
       references users

    insert
    into
        users
        (created_at, email, is_deleted, password, profile_url, updated_at, username, user_id)
    values
        (?, ?, ?, ?, ?, ?, ?, default)
Hibernate:
    insert
    into
        users
        (created_at, email, is_deleted, password, profile_url, updated_at, username, user_id)
    values
        (?, ?, ?, ?, ?, ?, ?, default)
2025-05-09T02:16:50.782+09:00 DEBUG 26956 --- [spring-daangn] [           main] org.hibernate.SQL                        :
    insert
    into
        posts
        (content, created_at, is_deleted, title, updated_at, user_id, post_id)
    values
        (?, ?, ?, ?, ?, ?, default)
Hibernate:
    insert
    into
        posts
        (content, created_at, is_deleted, title, updated_at, user_id, post_id)
    values
        (?, ?, ?, ?, ?, ?, default)
2025-05-09T02:16:50.784+09:00 DEBUG 26956 --- [spring-daangn] [           main] org.hibernate.SQL                        :
    insert
    into
        comments
        (content, created_at, is_deleted, parent_comment_id, post_id, updated_at, user_id, comment_id)
    values
        (?, ?, ?, ?, ?, ?, ?, default)
Hibernate:
    insert
    into
        comments
        (content, created_at, is_deleted, parent_comment_id, post_id, updated_at, user_id, comment_id)
    values
        (?, ?, ?, ?, ?, ?, ?, default)
2025-05-09T02:16:50.785+09:00 DEBUG 26956 --- [spring-daangn] [           main] org.hibernate.SQL                        :
    insert
    into
        comments
        (content, created_at, is_deleted, parent_comment_id, post_id, updated_at, user_id, comment_id)
    values
        (?, ?, ?, ?, ?, ?, ?, default)
Hibernate:
    insert
    into
        comments
        (content, created_at, is_deleted, parent_comment_id, post_id, updated_at, user_id, comment_id)
    values
        (?, ?, ?, ?, ?, ?, ?, default)
2025-05-09T02:16:50.786+09:00 DEBUG 26956 --- [spring-daangn] [           main] org.hibernate.SQL                        :
    insert
    into
        comments
        (content, created_at, is_deleted, parent_comment_id, post_id, updated_at, user_id, comment_id)
    values
        (?, ?, ?, ?, ?, ?, ?, default)
Hibernate:
    insert
    into
        comments
        (content, created_at, is_deleted, parent_comment_id, post_id, updated_at, user_id, comment_id)
    values
        (?, ?, ?, ?, ?, ?, ?, default)
2025-05-09T02:16:50.869+09:00 DEBUG 26956 --- [spring-daangn] [           main] org.hibernate.SQL                        :
    select
        c1_0.comment_id,
        c1_0.content,
        c1_0.created_at,
        c1_0.is_deleted,
        c1_0.parent_comment_id,
        c1_0.post_id,
        c1_0.updated_at,
        c1_0.user_id
    from
        comments c1_0
Hibernate:
    select
        c1_0.comment_id,
        c1_0.content,
        c1_0.created_at,
        c1_0.is_deleted,
        c1_0.parent_comment_id,
        c1_0.post_id,
        c1_0.updated_at,
        c1_0.user_id
    from
        comments c1_0

```

- DDL로 스키마를 생성 -> 데이터 삽입(insert) -> 데이터 조회(select)

> [!NOTE]
> CommentRepository.findAll() 호출 시 총 3개의 레코드 반환하는 것을 확인할 수 있다.
> **성공!**

---

# 번외 - 테스트 전략 비교
## Spring 기반 테스트 전략

| **테스트 종류** | **목적 및 범위** | **대표 기술 / 애노테이션** | **장점** | **단점** |
| --- | --- | --- | --- | --- |
| **단위 테스트** | 서비스 로직 검증(외부 의존성 제거) | `@ExtendWith(MockitoExtension.class)@Mock`, `@InjectMocks` | 빠르고 독립적<br>비즈니스 로직 검증에 최적 | DB 연동/쿼리 검증 불가 |
| **슬라이스 테스트** | JPA Repository만 단독 검증 | `@DataJpaTest` (+ H2) | JPA 쿼리와 CRUD 테스트에 특화<br>빠른 테스트 속도 | 서비스/컨트롤러는 테스트 불가 |
| **Testcontainers 통합 테스트** | 실제 DB 환경에서 JPA 검증 | `@DataJpaTest` or `@SpringBootTest`+ Testcontainers | 운영 DB와 유사한 환경<br>정확한 SQL/트랜잭션 검증 | 느린 속도, Docker 환경 필요 |
| **전체 통합 테스트** | 계층 간 연동 확인(Controller → Service → Repository) | `@SpringBootTest` | 실제 사용 흐름 테스트 가능<br>전체 시스템 검증 가능 | 느림원인 추적 어려움<br>의존성 많음 |
| **WebMvcTest** | Controller 로직만 검증 | `@WebMvcTest` + `@MockBean` | 웹 계층 단독 테스트 가능<br>빠름 | Service/Repository는 Mock이므로 실제 동작 아님 |
| **BDD 스타일 테스트** | 가독성 높은 테스트 | Mockito (`given-when-then`) | 의도 명확<br>협업·리뷰에 유리 | 기존 방식보다 작성이 복잡할 수 있음 |
| **REST API 테스트** | 실제 HTTP 기반 API 동작 확인 | `RestAssured`, `MockMvc` | API 응답 정확성 및 흐름 검증 가능 | 느림<br>설정 복잡할 수 있음 |

## 선택 가이드

| 목적 | 추천 방법 |
| --- | --- |
| 서비스 로직만 빠르게 검증하고 싶다 | Mockito 단위 테스트 |
| Repository 쿼리가 정확한지 테스트하고 싶다 | `@DataJpaTest` or Testcontainers |
| 실제 DB에서 운영 환경과 유사하게 테스트하고 싶다 | Testcontainers 통합 테스트 |
| 전체 기능 흐름(Controller → DB) 테스트 필요 | `@SpringBootTest` |
| Controller 입력/응답만 검증하고 싶다 | `@WebMvcTest` |

## 테스트 종류 이름의 의미와 설명

| **테스트 종류** | **이름의 의미 / 정의** | **테스트 목적 및 특징** | **주 대상 계층 또는 범위** |
| --- | --- | --- | --- |
| **단위 테스트 (Unit Test)** | *"하나의 단위(메서드 또는 클래스)"를 고립시켜 테스트* | 외부 의존성을 제거한 상태에서, 하나의 기능 로직만 검증 | Service, Domain (비즈니스 로직) |
| **슬라이스 테스트 (Slice Test)** | *"애플리케이션의 일부 계층만 잘라서 테스트"* | 특정 계층(예: Repository, Controller)만 로드하고 테스트 | Repository (`@DataJpaTest`), Controller (`@WebMvcTest`) 등 |
| **통합 테스트 (Integration Test)** | *여러 계층이 함께 연동되는지 확인하는 테스트* | DB, 서비스, 컨트롤러 등 실제 흐름과 통합 동작을 검증 | Service ↔ Repository, Controller ↔ Service 등 |
| **전체 통합 테스트 (Full Integration Test)** | *스프링 부트 앱 전체를 기동해 테스트* | 실제 실행과 가장 가까운 환경에서 모든 컴포넌트를 함께 테스트 | 전체 (Controller → Service → Repository) |
| **BDD 테스트** | *Behavior-Driven Development*“행동 주도 개발” 방식 | `given-when-then` 형태로 의도를 명확히 표현하며 테스트 | 전 계층 가능 (Mockito 사용 시 주로 Service) |
| **REST API 테스트** | 실제 HTTP 요청을 통해 API 동작 확인 | API의 Request/Response, 상태코드, JSON 구조 등을 검증 | Controller, Spring REST API 전체 흐름 |

## 현업에서 가장 자주 쓰는 Spring 테스트 TOP 3
1. **단위 테스트 (Mockito 기반)** → `Service` 계층 테스트
2. **슬라이스 테스트 (`@DataJpaTest`)** → `Repository` 계층 검증
3. **통합 테스트 (`@SpringBootTest`)** → 전체 흐름 검증

## 1. **단위 테스트 (Service 계층, Mockito 사용)**

### 사용 목적:

비즈니스 로직만 검증하고, Repository는 모킹(mock)으로 처리해 외부 의존성 제거.

### 코드 예시:

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void 사용자명으로_조회하면_정상적으로_리턴된다() {
        // Given
        String username = "testuser";
        User user = new User(username, "test@example.com");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        UserDto userDto = userService.findUserByUsername(username);

        // Then
        assertThat(userDto.getUsername()).isEqualTo("testuser");
        verify(userRepository).findByUsername(username); // 호출 여부 검증
    }
}
```

---

## 2. **슬라이스 테스트 (`@DataJpaTest`)**

### 사용 목적:

JPA Repository에서 JPQL/쿼리 동작이 잘 되는지 빠르게 검증.

### 코드 예시:

```java
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void 사용자명을_기반으로_조회할_수_있다() {
        // Given
        User saved = userRepository.save(new User("testuser", "test@example.com"));

        // When
        Optional<User> found = userRepository.findByUsername("testuser");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }
}
```

---

## 3. **통합 테스트 (`@SpringBootTest`)**

### 사용 목적:

Controller → Service → Repository 전체 흐름을 실제처럼 테스트.

REST API 전체 동작도 같이 확인 가능.

### 코드 예시:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.save(new User("testuser", "test@example.com"));
    }

    @Test
    void 사용자명으로_사용자정보_API_조회() throws Exception {
        mockMvc.perform(get("/api/users/testuser"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}
```

---

## 결론

| **테스트** | **주 대상** | **특징** | **실제 현업 사용도** |
| --- | --- | --- | --- |
| 단위 테스트 (Mockito) | Service | 빠르고 독립적 | ⭐⭐⭐⭐ |
| 슬라이스 테스트 (`@DataJpaTest`) | Repository | 쿼리 검증에 최적 | ⭐⭐⭐⭐ |
| 통합 테스트 (`@SpringBootTest`) | 전체 흐름 | 전체 연동 확인 | ⭐⭐⭐ |

```jsx
# 전체 슬라이스 테스트 실행
./gradlew test --tests "com.trip.xplore.user.repository.UserRepositoryTest"

# 특정 테스트 메소드만 실행
./gradlew test --tests "com.trip.xplore.user.repository.UserRepositoryTest.이메일로_사용자_찾기"
```
