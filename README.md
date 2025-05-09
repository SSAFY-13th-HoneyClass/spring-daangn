# 1️⃣ 당근마켓의 DB를 모델링해요

## 당근마켓이란?

> 당근마켓은 중고 거래 플랫폼 중 하나이며 국내 1위 중고 거래 플랫폼이다.
> 

### 주요 기능

- 사용자 간의 중고 거래
    - 거래 물품 게시글 등록
    - 거래 물품 관련 1:1 채팅
- 커뮤니티 기능
    - 게시판 게시글 등록
    - 좋아요, 북마크, 공감, 댓글, 대댓글 등 기능 제공
- 동네 기반 서비스
    - 사용자 동네 등록 (최대 2개)
    - 위치 기반 동네 인증
- 그 외
    - 수 많은 기능이 더 있지만 이번 미션은 위의 기능을 위주로 분석

## ERD 분석

<aside>
⚠️

**신경 쓴 점**

- 실제 어플리케이션을 사용해보며 기능 분석
- 각 테이블간의 연관성이 너무 높지 않도록 최대한 분리
- 각 테이블마다 고유 UID(Unique ID)를 가지도록 함
</aside>

### ERD 사진

![당근마켓 ERD](https://github.com/user-attachments/assets/0d3dc74c-6b2d-4f1b-9100-3060a97eca17)

### ERD 상세

### 📌 `Users` 테이블

- 기본적인 회원 정보를 담고 있는 테이블
- role에 따라 권한을 분리
- 사용자 프로필 필드는 추후에 따로 테이블로 분리해도 될 듯?

| 용도 | column명 | type |
| --- | --- | --- |
| 회원 고유 ID | uuid | BIGINT, PK, AUTO_INCREMENT |
| 회원 ID | id | VARCHAR(100), NOT NULL |
| 회원 PW | password | VARCHAR(255), NULL |
| 이름 | name | VARCHAR(50), NULL |
| 닉네임 | nickname | VARCHAR(50), NOT NULL |
| 전화번호 | phone | VARCHAR(20), NOT NULL |
| 프로필 이미지 | profile_img | VARCHAR(255), NULL, DEFAULT='DEFAULT_PROFILE_IMG_URL' |
| 매너 온도 | manner | DECIMAL(4,1), NULL, DEFAULT=36.5 |
| 최근 활동 날짜 | lastest | TIMESTAMP, NULL, DEFAULT CURRENT_TIMESTAMP |
| 가입 일자 | created | TIMESTAMP, NULL, DEFAULT CURRENT_TIMESTAMP |
| 권한 | role | VARCHAR(10), DEFAULT='USER' |

### 📌 `Locations` 테이블

- 지역 정보를 담고 있는 테이블
- 추후에 시, 구군, 읍/면/동 정보도 따로 테이블로 빼서 관리해도 될 것 같음

| 용도 | column명 | type |
| --- | --- | --- |
| 지역 고유 ID | luid | BIGINT, PK, AUTO_INCREMENT |
| 시 | si | VARCHAR(100), NOT NULL |
| 구 | gugun | VARCHAR(100), NOT NULL |
| 읍/면/동 | location | VARCHAR(255), NOT NULL |
| 위도 | lat | DECIMAL(10,7), NOT NULL |
| 경도 | lng | DECIMAL(10,7), NOT NULL |

### 📌 `UserLocations` 테이블

- 사용자가 가지고 있는 우리 동네 테이블 (최대 2개까지 보유 가능)
- 어플을 보면 동네 범위는 1단계부터 최대 4단계까지 가능
    - 범위는 어떤 식으로 잡는지는 잘 모르겠다…

| 용도 | column명 | type |
| --- | --- | --- |
| 유저 동네 고유 ID | uluid | BIGINT, PK, AUTO_INCREMENT |
| 회원 고유 ID | user_id | BIGINT, FK → Users(uuid), NOT NULL |
| 지역 고유 ID | location_id | BIGINT, FK → Locations(luid), NOT NULL |
| 동네 범위 | range | INT, DEFAULT=1 |
| 대표 동네 여부 | rep | BOOLEAN, DEFAULT=FALSE |
| 우리 동네 인증 여부 | auth | BOOLEAN, DEFAULT=FALSE |
| 최근 우리 동네 인증 날짜 | lastest_auth | TIMESTAMP, NULL |

### 📌 `Posts` 테이블

- 게시글 정보를 담는 테이블
- 첨부 데이터를 우선 문자 형식으로 저장
    - 추후에 투표 데이터는 별도 테이블로 빼야 할 듯

| 용도 | column명 | type |
| --- | --- | --- |
| 게시글 고유 ID | puid | BIGINT, PK, AUTO_INCREMENT |
| 작성자 | user_id | BIGINT, FK → Users(uuid), NOT NULL |
| 주제 | subject | VARCHAR(255), NOT NULL |
| 제목 | title | VARCHAR(255), NOT NULL |
| 본문 | content | TEXT, NOT NULL |
| 첨부 장소 | post_location | VARCHAR(255), NULL |
| 첨부 투표 | post_vote | BOOLEAN, NULL |
| 첨부 태그 | post_tag | VARCHAR(255), NULL |
| 인기글 여부 | hot | BOOLEAN, DEFAULT=FALSE |
| 게시 날짜 | created | TIMESTAMP, DEFAULT CURRENT_TIMESTAMP |
| 조회수 | views | INT, DEFAULT=0 |
| 저장수 | bookmarks | INT, DEFAULT=0 |

### 📌 `PostComments` 테이블

- 게시글 댓글 정보 테이블

| 용도 | column명 | type |
| --- | --- | --- |
| 게시글 댓글 고유 ID | pcuid | BIGINT, PK, AUTO_INCREMENT |
| 게시글 ID | post_id | BIGINT, FK → Posts(puid), NOT NULL |
| 작성자 | user_id | BIGINT, FK → Users(uuid), NOT NULL |
| 본문 | content | TEXT, NOT NULL |
| 좋아요 수 | likes | INT, DEFAULT=0 |
| 답글 수 | recomments | INT, DEFAULT=0 |

### 📌 `PostEmpathys` 테이블

- 게시글 공감 테이블
- 게시글 공감의 종류는 총 4개로 되있는데 이를 번호로 나눔

| 용도 | column명 | type |
| --- | --- | --- |
| 게시글 공감 고유 ID | peuid | BIGINT, PK, AUTO_INCREMENT |
| 공감 종류 | empathy | INT, DEFAULT=1 (1~4: 따봉, 하트 등) |
| 게시글 ID | post_id | BIGINT, FK → Posts(puid), NOT NULL |
| 작성자 | user_id | BIGINT, FK → Users(uuid), NOT NULL |

### 📌 `PostImages` 테이블

- 게시글 이미지 테이블
- 게시글에 이미지가 첨부되 있다면 여기서 가져다 보여줌

| 용도 | column명 | type |
| --- | --- | --- |
| 게시글 이미지 고유 ID | piuid | BIGINT, PK, AUTO_INCREMENT |
| 게시글 ID | post_id | BIGINT, FK → Posts(puid), NOT NULL |
| 게시글 이미지 | post_img | VARCHAR(255), NOT NULL |

### 📌 `PostReComments` 테이블

- 게시글 댓글 대댓글 테이블
- 댓글의 댓글을 관리해주는 테이블 (댓글의 댓글까지만 가능!)

| 용도 | column명 | type |
| --- | --- | --- |
| 게시글 대댓글 고유 ID | prcuid | BIGINT, PK, AUTO_INCREMENT |
| 본문 | content | TEXT, NOT NULL |
| 좋아요 수 | likes | INT, DEFAULT=0 |
| 댓글 ID | post_id | BIGINT, FK → Posts(puid), NOT NULL |
| 작성자 | user_id | BIGINT, FK → Users(uuid), NOT NULL |

### 📌 `PostCommentLikes` 테이블

- 댓글 좋아요 관리 테이블
- 내가 좋아요 누른 댓글은 표시를 할 수 있기 위해 존재

| 용도 | column명 | type |
| --- | --- | --- |
| 게시글 댓글 좋아요 고유 ID | pcluid | BIGINT, PK, AUTO_INCREMENT |
| 좋아요한 회원 ID | user_id | BIGINT, FK → Users(uuid), NOT NULL |
| 게시글 ID | post_id | BIGINT, FK → Posts(puid), NOT NULL |
| 댓글 ID | comment_id | BIGINT, FK → PostComments(pcuid), NOT NULL |
| 대댓글 ID | recomment_id | BIGINT, FK → PostReComments(prcuid), NULL |

### 📌 `Products` 테이블

- 판매 상품 글 관리 테이블

| 용도 | column명 | type |
| --- | --- | --- |
| 상품 판매 글 고유 ID | puid | BIGINT, PK, AUTO_INCREMENT |
| 거래 장소 | location_id | BIGINT, FK → Locations(luid), NOT NULL |
| 작성자 | user_id | BIGINT, FK → Users(uuid), NOT NULL |
| 제목 | title | VARCHAR(255), NOT NULL |
| 카테고리 | category | VARCHAR(100), NULL |
| 본문 | content | TEXT, NOT NULL |
| 거래 방식 | deal_type | VARCHAR(50), DEFAULT='판매하기' |
| 가격 | price | INT, DEFAULT=0 |
| 작성 시간 | created | TIMESTAMP, DEFAULT CURRENT_TIMESTAMP |
| 조회수 | views | INT, DEFAULT=0 |
| 거래 완료 여부 | isSell | BOOLEAN, DEFAULT=FALSE |

### 📌 `ProductImages` 테이블

- 판매 상품 글 이미지 관리 테이블

| 용도 | column명 | type |
| --- | --- | --- |
| 상품 사진 고유 ID | piuid | BIGINT, PK, AUTO_INCREMENT |
| 상품 ID | puid | BIGINT, FK → Products(puid), NOT NULL |
| 상품 이미지 | product_img | VARCHAR(255), NOT NULL |

### 📌 `ProductLikes` 테이블

- 관심 판매 상품 관리 테이블
- 사용자가 관심(좋아요)누른 판매 상품 글을 표시하기 위해

| 용도 | column명 | type |
| --- | --- | --- |
| 상품 관심 고유 ID | pluid | BIGINT, PK, AUTO_INCREMENT |
| 관심 누른 회원 ID | user_id | BIGINT, FK → Users(uuid), NOT NULL |
| 상품 ID | product_id | BIGINT, FK → Products(puid), NOT NULL |

### 📌 `Chattings` 테이블

- 사용자 1:1 채팅방 관리 테이블
- 판매 상품을 기준으로 채팅방이 생성됨

| 용도 | column명 | type |
| --- | --- | --- |
| 채팅방 고유 ID | cuid | BIGINT, PK, AUTO_INCREMENT |
| 상품 ID | product_id | BIGINT, FK → Products(puid), NOT NULL |
| 판매 유저 ID | sell_user_id | BIGINT, FK → Users(uuid), NOT NULL |
| 구매 유저 ID | buy_user_id | BIGINT, FK → Users(uuid), NOT NULL |
| 생성 날짜 | created | TIMESTAMP, DEFAULT CURRENT_TIMESTAMP |
| 마지막 대화 날짜 | lastest | TIMESTAMP, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

### 📌 `ChattingLogs` 테이블

- 채팅 로그 관리 테이블
- 채팅방에서 오가는 대화 내용을 기록해두는 테이블
- 추후 본문 타입에 따라 테이블을 나누어야 할 것 같음

| 용도 | column명 | type |
| --- | --- | --- |
| 채팅방 로그 고유 ID | cluid | BIGINT, PK, AUTO_INCREMENT |
| 채팅방 ID | chatting_id | BIGINT, FK → Chattings(cuid), NOT NULL |
| 작성자 ID | user_id | BIGINT, FK → Users(uuid), NOT NULL |
| 생성 날짜 | created | TIMESTAMP, DEFAULT CURRENT_TIMESTAMP |
| 읽음 여부 | check | BOOLEAN, DEFAULT FALSE |
| 본문 | content | TEXT, NOT NULL |
| 본문 타입 | content_type | VARCHAR(50), DEFAULT='TEXT' |

## Spring 구현

> 위에서 작성한 ERD를 기반으로 Spring에서 Domain과 Repository를 작성
> 

<aside>
⚠️

기본적인 양식은 모두 유사하기에 User, Post, Product 3개의 테이블만 보도록 하겠습니다.

JPA를 통해 간단한 CRUD 구현

</aside>

### Domain

### 🛠User

```java
package com.example.daangn.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uuid;

    @Column(nullable = false)
    private String id;

    private String password;

    private String name;

    private String nickname;

    @Column(nullable = false)
    private String phone;

    private String profileImg;

    private BigDecimal manner;

    private LocalDateTime lastest;

    private LocalDateTime created;

    @Column(nullable = false)
    private String role;
}

```

### 🛠Post

```java
package com.example.daangn.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long puid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String postLocation;

    private Boolean postVote;

    private String postTag;

    private Boolean hot;

    @Column(nullable = false)
    private LocalDateTime created;

    private Integer views;

    private Integer bookmarks;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostEmpathy> empathies = new ArrayList<>();
}

```

### 🛠Product

```java
package com.example.daangn.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long puid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String dealType;

    private Integer price;

    @Column(nullable = false)
    private LocalDateTime created;

    private Integer views;

    private Boolean isSell;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chatting> chattings = new ArrayList<>();
}

```

### Rpository

### 📂User

```java
package com.example.daangn.repository;

import com.example.daangn.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<User> findById(String id);
    boolean existsById(String id);
    Optional<User> findByPhone(String phone);
}
```

### 📂Post

```java
package com.example.daangn.repository;

import com.example.daangn.domain.Post;
import com.example.daangn.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUser(User user, Pageable pageable);
    Page<Post> findByHot(Boolean hot, Pageable pageable);
    Page<Post> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword, Pageable pageable);
    Page<Post> findByPostTagContaining(String tag, Pageable pageable);
    List<Post> findTop10ByOrderByViewsDesc();
}

```

### 📂Product

```java
package com.example.daangn.repository;

import com.example.daangn.domain.Location;
import com.example.daangn.domain.Product;
import com.example.daangn.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByIsSell(Boolean isSell, Pageable pageable);
    Page<Product> findByUser(User user, Pageable pageable);
    Page<Product> findByLocation(Location location, Pageable pageable);
    Page<Product> findByCategory(String category, Pageable pageable);
    Page<Product> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword, Pageable pageable);
    List<Product> findTop10ByOrderByViewsDesc();
}

```

# 2️⃣ Repository 단위 테스트를 진행해요

> Repository 계층의 단위 테스트를 진행해보자!
- 대상 Repository : Posts (FK = Users 테이블)
- Junit 기반 테스트 이며 given, when, then 조건에 따라 테스트 진행
- 테스트시 DB 동작 확인을 위해 임시적으로 h2 db 사용
> 

### 테스트

### 1. Post 엔티티 저장 및 조회 테스트

```java
    @Test
    @DisplayName("Post 엔티티 저장 및 조회 테스트")
    void saveAndFindTest() {
        // given
        Post post1 = createPost("첫 번째 게시글", "첫 번째 내용입니다.", "공지사항");
        Post post2 = createPost("두 번째 게시글", "두 번째 내용입니다.", "질문");
        Post post3 = createPost("세 번째 게시글", "세 번째 내용입니다.", "자유");

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // when
        List<Post> foundPosts = postRepository.findAll();

        // then
        assertThat(foundPosts).isNotNull();
        assertThat(foundPosts.size()).isEqualTo(3);
        assertThat(foundPosts).extracting("title")
                .containsExactlyInAnyOrder("첫 번째 게시글", "두 번째 게시글", "세 번째 게시글");
    }
```

### 2. Post 엔티티 ID로 조회 테스트

```java
    @Test
    @DisplayName("Post 엔티티 ID로 조회 테스트")
    void findByIdTest() {
        // given
        Post post = createPost("테스트 게시글", "테스트 내용입니다.", "공지사항");
        postRepository.save(post);

        // when
        Optional<Post> foundPost = postRepository.findById(post.getPuid());

        // then
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo("테스트 게시글");
        assertThat(foundPost.get().getContent()).isEqualTo("테스트 내용입니다.");
    }
```

### 3. Post 사용자별 조회 테스트

```java
    @Test
    @DisplayName("Post 사용자별 조회 테스트")
    void findByUserTest() {
        // given
        // 첫 번째 사용자의 게시글
        Post post1 = createPost("첫 번째 게시글", "첫 번째 내용입니다.", "공지사항");
        Post post2 = createPost("두 번째 게시글", "두 번째 내용입니다.", "질문");
        Post post3 = createPost("세 번째 게시글", "세 번째 내용입니다.", "자유");

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // 두 번째 사용자 생성 및 게시글 작성
        User anotherUser = User.builder()
                .id("anotheruser")
                .password("password")
                .name("Another User")
                .nickname("another")
                .phone("01098765432")
                .manner(new BigDecimal("36.5"))
                .role("USER")
                .build();

        usersRepository.save(anotherUser);

        Post anotherPost = Post.builder()
                .user(anotherUser)
                .subject("다른 주제")
                .title("다른 사용자의 게시글")
                .content("다른 사용자가 작성한 내용입니다.")
                .created(LocalDateTime.now())
                .views(0)
                .bookmarks(0)
                .build();

        postRepository.save(anotherPost);

        // when
        Page<Post> foundPosts = postRepository.findByUser(testUser,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created")));

        // then
        assertThat(foundPosts).isNotNull();
        assertThat(foundPosts.getTotalElements()).isEqualTo(3);
        assertThat(foundPosts.getContent()).extracting("title")
                .containsExactlyInAnyOrder("첫 번째 게시글", "두 번째 게시글", "세 번째 게시글");
    }
```

### 4. Post 업데이트 테스트

```java
    @Test
    @DisplayName("Post 업데이트 테스트")
    void updatePostTest() {
        // given
        Post post = createPost("원래 제목", "원래 내용", "공지사항");
        postRepository.save(post);

        // when
        post.setTitle("수정된 제목");
        post.setContent("수정된 내용");
        postRepository.save(post);

        // then
        Optional<Post> updatedPost = postRepository.findById(post.getPuid());
        assertThat(updatedPost).isPresent();
        assertThat(updatedPost.get().getTitle()).isEqualTo("수정된 제목");
        assertThat(updatedPost.get().getContent()).isEqualTo("수정된 내용");
    }
```

### 5. Post 삭제 테스트

```java
    @Test
    @DisplayName("Post 삭제 테스트")
    void deletePostTest() {
        // given
        Post post1 = createPost("첫 번째 게시글", "첫 번째 내용입니다.", "공지사항");
        Post post2 = createPost("두 번째 게시글", "두 번째 내용입니다.", "질문");
        Post post3 = createPost("삭제할 게시글", "삭제할 내용입니다.", "자유");

        postRepository.saveAll(List.of(post1, post2, post3));

        // when
        postRepository.delete(post3);

        // then
        List<Post> remainingPosts = postRepository.findAll();
        assertThat(remainingPosts.size()).isEqualTo(2);
        assertThat(remainingPosts).extracting("title")
                .containsExactlyInAnyOrder("첫 번째 게시글", "두 번째 게시글");

        Optional<Post> deletedPost = postRepository.findById(post3.getPuid());
        assertThat(deletedPost).isNotPresent();
    }
```

### 추가1). 키워드로 게시글 검색 테스트

```java
    @Test
    @DisplayName("키워드로 게시글 검색 테스트")
    void searchPostsByKeywordTest() {
        // given
        Post post1 = createPost("자바 프로그래밍", "자바 프로그래밍에 대한 내용입니다.", "개발");
        Post post2 = createPost("파이썬 프로그래밍", "파이썬 프로그래밍에 대한 내용입니다.", "개발");
        Post post3 = createPost("스프링 부트", "자바 기반의 스프링 부트 프레임워크입니다.", "개발");

        postRepository.saveAll(List.of(post1, post2, post3));

        // when
        Page<Post> javaResults = postRepository.findByTitleContainingOrContentContaining(
                "자바", "자바", PageRequest.of(0, 10));

        // then
        assertThat(javaResults.getTotalElements()).isEqualTo(2);
        assertThat(javaResults.getContent()).extracting("title")
                .containsExactlyInAnyOrder("자바 프로그래밍", "스프링 부트");
    }
```

### 추가2). 인기 게시글 조회 테스트

```java
    @Test
    @DisplayName("인기 게시글 조회 테스트")
    void findTopPostsByViewsTest() {
        // given
        Post post1 = createPost("인기 게시글 1", "내용 1", "자유");
        post1.setViews(100);

        Post post2 = createPost("인기 게시글 2", "내용 2", "자유");
        post2.setViews(50);

        Post post3 = createPost("인기 게시글 3", "내용 3", "자유");
        post3.setViews(200);

        Post post4 = createPost("인기 게시글 4", "내용 4", "자유");
        post4.setViews(30);

        Post post5 = createPost("인기 게시글 5", "내용 5", "자유");
        post5.setViews(150);

        postRepository.saveAll(List.of(post1, post2, post3, post4, post5));

        // when
        List<Post> topPosts = postRepository.findTop10ByOrderByViewsDesc();

        // then
        assertThat(topPosts).hasSize(5);
        assertThat(topPosts.get(0).getTitle()).isEqualTo("인기 게시글 3");  // 200 views
        assertThat(topPosts.get(1).getTitle()).isEqualTo("인기 게시글 5");  // 150 views
        assertThat(topPosts.get(2).getTitle()).isEqualTo("인기 게시글 1");  // 100 views
    }
```

### 전체 Test 코드

```java
package com.example.daangn.repository;

import com.example.daangn.domain.Post;
import com.example.daangn.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UsersRepository usersRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = User.builder()
                .id("testuser")
                .password("password")
                .name("Test User")
                .nickname("tester")
                .phone("01012345678")
                .manner(new BigDecimal("36.5"))
                .role("USER")
                .build();

        usersRepository.save(testUser);
    }

    @Test
    @DisplayName("Post 엔티티 저장 및 조회 테스트")
    void saveAndFindTest() {
        // given
        Post post1 = createPost("첫 번째 게시글", "첫 번째 내용입니다.", "공지사항");
        Post post2 = createPost("두 번째 게시글", "두 번째 내용입니다.", "질문");
        Post post3 = createPost("세 번째 게시글", "세 번째 내용입니다.", "자유");

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // when
        List<Post> foundPosts = postRepository.findAll();

        // then
        assertThat(foundPosts).isNotNull();
        assertThat(foundPosts.size()).isEqualTo(3);
        assertThat(foundPosts).extracting("title")
                .containsExactlyInAnyOrder("첫 번째 게시글", "두 번째 게시글", "세 번째 게시글");
    }

    @Test
    @DisplayName("Post 엔티티 ID로 조회 테스트")
    void findByIdTest() {
        // given
        Post post = createPost("테스트 게시글", "테스트 내용입니다.", "공지사항");
        postRepository.save(post);

        // when
        Optional<Post> foundPost = postRepository.findById(post.getPuid());

        // then
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo("테스트 게시글");
        assertThat(foundPost.get().getContent()).isEqualTo("테스트 내용입니다.");
    }

    @Test
    @DisplayName("Post 사용자별 조회 테스트")
    void findByUserTest() {
        // given
        // 첫 번째 사용자의 게시글
        Post post1 = createPost("첫 번째 게시글", "첫 번째 내용입니다.", "공지사항");
        Post post2 = createPost("두 번째 게시글", "두 번째 내용입니다.", "질문");
        Post post3 = createPost("세 번째 게시글", "세 번째 내용입니다.", "자유");

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // 두 번째 사용자 생성 및 게시글 작성
        User anotherUser = User.builder()
                .id("anotheruser")
                .password("password")
                .name("Another User")
                .nickname("another")
                .phone("01098765432")
                .manner(new BigDecimal("36.5"))
                .role("USER")
                .build();

        usersRepository.save(anotherUser);

        Post anotherPost = Post.builder()
                .user(anotherUser)
                .subject("다른 주제")
                .title("다른 사용자의 게시글")
                .content("다른 사용자가 작성한 내용입니다.")
                .created(LocalDateTime.now())
                .views(0)
                .bookmarks(0)
                .build();

        postRepository.save(anotherPost);

        // when
        Page<Post> foundPosts = postRepository.findByUser(testUser,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created")));

        // then
        assertThat(foundPosts).isNotNull();
        assertThat(foundPosts.getTotalElements()).isEqualTo(3);
        assertThat(foundPosts.getContent()).extracting("title")
                .containsExactlyInAnyOrder("첫 번째 게시글", "두 번째 게시글", "세 번째 게시글");
    }

    @Test
    @DisplayName("Post 업데이트 테스트")
    void updatePostTest() {
        // given
        Post post = createPost("원래 제목", "원래 내용", "공지사항");
        postRepository.save(post);

        // when
        post.setTitle("수정된 제목");
        post.setContent("수정된 내용");
        postRepository.save(post);

        // then
        Optional<Post> updatedPost = postRepository.findById(post.getPuid());
        assertThat(updatedPost).isPresent();
        assertThat(updatedPost.get().getTitle()).isEqualTo("수정된 제목");
        assertThat(updatedPost.get().getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("Post 삭제 테스트")
    void deletePostTest() {
        // given
        Post post1 = createPost("첫 번째 게시글", "첫 번째 내용입니다.", "공지사항");
        Post post2 = createPost("두 번째 게시글", "두 번째 내용입니다.", "질문");
        Post post3 = createPost("삭제할 게시글", "삭제할 내용입니다.", "자유");

        postRepository.saveAll(List.of(post1, post2, post3));

        // when
        postRepository.delete(post3);

        // then
        List<Post> remainingPosts = postRepository.findAll();
        assertThat(remainingPosts.size()).isEqualTo(2);
        assertThat(remainingPosts).extracting("title")
                .containsExactlyInAnyOrder("첫 번째 게시글", "두 번째 게시글");

        Optional<Post> deletedPost = postRepository.findById(post3.getPuid());
        assertThat(deletedPost).isNotPresent();
    }

    @Test
    @DisplayName("키워드로 게시글 검색 테스트")
    void searchPostsByKeywordTest() {
        // given
        Post post1 = createPost("자바 프로그래밍", "자바 프로그래밍에 대한 내용입니다.", "개발");
        Post post2 = createPost("파이썬 프로그래밍", "파이썬 프로그래밍에 대한 내용입니다.", "개발");
        Post post3 = createPost("스프링 부트", "자바 기반의 스프링 부트 프레임워크입니다.", "개발");

        postRepository.saveAll(List.of(post1, post2, post3));

        // when
        Page<Post> javaResults = postRepository.findByTitleContainingOrContentContaining(
                "자바", "자바", PageRequest.of(0, 10));

        // then
        assertThat(javaResults.getTotalElements()).isEqualTo(2);
        assertThat(javaResults.getContent()).extracting("title")
                .containsExactlyInAnyOrder("자바 프로그래밍", "스프링 부트");
    }

    @Test
    @DisplayName("인기 게시글 조회 테스트")
    void findTopPostsByViewsTest() {
        // given
        Post post1 = createPost("인기 게시글 1", "내용 1", "자유");
        post1.setViews(100);

        Post post2 = createPost("인기 게시글 2", "내용 2", "자유");
        post2.setViews(50);

        Post post3 = createPost("인기 게시글 3", "내용 3", "자유");
        post3.setViews(200);

        Post post4 = createPost("인기 게시글 4", "내용 4", "자유");
        post4.setViews(30);

        Post post5 = createPost("인기 게시글 5", "내용 5", "자유");
        post5.setViews(150);

        postRepository.saveAll(List.of(post1, post2, post3, post4, post5));

        // when
        List<Post> topPosts = postRepository.findTop10ByOrderByViewsDesc();

        // then
        assertThat(topPosts).hasSize(5);
        assertThat(topPosts.get(0).getTitle()).isEqualTo("인기 게시글 3");  // 200 views
        assertThat(topPosts.get(1).getTitle()).isEqualTo("인기 게시글 5");  // 150 views
        assertThat(topPosts.get(2).getTitle()).isEqualTo("인기 게시글 1");  // 100 views
    }

    // 테스트 도우미 메소드
    private Post createPost(String title, String content, String subject) {
        return Post.builder()
                .user(testUser)
                .subject(subject)
                .title(title)
                .content(content)
                .created(LocalDateTime.now())
                .views(0)
                .bookmarks(0)
                .build();
    }
}

```

### 테스트 결과

- 7개의 테스트가 모두 성공한 것을 확인!
![image](https://github.com/user-attachments/assets/b6e9b1c7-38b5-47f0-9f15-3c9dd476c3d4)


# 3️⃣ (옵션) JPA 관련 문제 해결

<aside>
📒

AI(클로드) 및 구글링 하여 공부한 내용들을 기입했습니다.

</aside>

## 1. 어떻게  data jpa는 interface만으로도 함수가 구현이 되는가?

- 참고 블로그
    
    https://pingpongdev.tistory.com/25
    

### ✅ **핵심**

> Spring은 Repository 인터페이스를 기반으로 동적으로 프록시 객체를 생성하고, 내부적으로 SimpleJpaRepository에 위임하여 동작하게 한다.
> 

---

### 🧩 **동작 과정 요약**

1. **Repository 인터페이스 선언**
    
    ```java
    java
    public interface MemberRepository extends JpaRepository<Member, Long> {
        List<Member> findAllByName(String name);
    }
    ```
    
    - 구현 클래스 없이도 동작 가능.
2. **Spring이 Proxy 객체 생성**
    - 런타임 시, `ProxyFactory`를 통해 프록시 객체 생성.
    - 실제 구현은 `SimpleJpaRepository`가 담당.
    - 사용자 정의 메서드 (`findAllByName`)도 메서드명 규칙 기반으로 프록시 내부에서 동적으로 처리.
3. **Reflection (리플렉션)**
    - Java의 `Reflection API`를 활용하여 클래스, 메서드, 필드 정보에 접근.
    - 동적으로 객체 생성, 메서드 실행, 필드 값 조작 가능.
4. **Dynamic Proxy**
    - `InvocationHandler`를 구현해 메서드 호출 시 로직을 가로채고 위임.
    - Java의 `Proxy.newProxyInstance(...)` 또는 Spring의 `ProxyFactory` 사용.
5. **Spring 내부 작동 흐름**
    - `RepositoryFactorySupport` → `getRepository(...)`
        - 프록시 인터페이스 설정
        - `SimpleJpaRepository`를 타겟으로 설정
        - 최종 프록시 객체를 반환하여 Bean으로 등록

---

### 🧪 디버깅 시 확인 사항

- `memberRepository`의 정체는 실제 `SimpleJpaRepository`를 타겟으로 가진 **프록시 객체**.
- 사용자의 `MemberRepository`는 단지 인터페이스지만, 프록시가 이를 구현한 것처럼 동작.

---

### 🙋‍♂️ 왜 이렇게 하는가?

- **개발자가 구현 없이 선언만 하면** 자동으로 스프링이 내부 구현을 제공.
- 메서드 이름 규칙 기반 쿼리 자동 생성.
- AOP, 트랜잭션, 커스텀 구현 등과의 유연한 통합 가능.

---

### ✅ 결론 정리 한 줄

> Spring Data JPA는 리플렉션과 동적 프록시 기술을 사용해, 인터페이스만으로도 구현체 없이 자동으로 동작하는 Repository Bean을 생성하고 주입해준다.
> 

## 2. EntityManager 생성자 주입과 싱글톤 빈의 관계

### ❓문제:

> data jpa를 찾다보면 SimpleJpaRepository에서  entity manager를 생성자 주입을 통해서 주입 받는다. 근데 싱글톤 객체는 한번만 할당을  받는데, 한번 연결 때 마다 생성이 되는 entity manager를 생성자 주입을 통해서 받는 것은 수상하지 않는가? 어떻게 되는 것일까?
> 

### ✅ 문제 파악:

- Spring의 **프록시 객체**와 **스코프**에 관한 것.
- SimpleJpaRepository는 EntityManager를 생성자 주입으로 받지만, 매 요청마다 다른 EntityManager를 사용

### ✅ 가능한 이유:

1. EntityManager는 실제로 **프록시 객체**를 주입받습니다. 이 프록시는 `SharedEntityManagerCreator`에 의해 생성됩니다.
2. 이 프록시는 현재 실행 중인 트랜잭션과 연결된 실제 EntityManager를 찾아주는 역할을 합니다.
3. Spring은 `TransactionSynchronizationManager`를 사용하여 현재 스레드의 트랜잭션에 바인딩된 EntityManager를 가져옵니다.

### ✅ 정리:

SimpleJpaRepository는 싱글톤이지만, 실제로 사용하는 EntityManager는 프록시를 통해 현재 트랜잭션 컨텍스트에 맞는 것을 동적으로 가져옵니다. 이것이 **컨테이너 관리 영속성 컨텍스트(Container-Managed Persistence Context)** 패턴입니다.

## 3. fetch join(N+1) 할 때 distinct를 안하면 생길 수 있는 문제

- **Fetch Join**: 지연 로딩으로 인한 N+1 문제를 해결하기 위해 사용되는 즉시 로딩 방식.
    
    ```sql
    select m from Member m join fetch m.team
    ```
    
- 하지만 **1:N 관계**에서 Fetch Join을 사용할 경우, 부모 엔티티가 자식 수만큼 중복 조회됨.
    - 예: 하나의 Team에 3명의 Member → Team A가 3번 조회됨.
- 해결: `distinct`를 사용하여 JPA가 중복된 엔티티를 제거하도록 지시
    
    ```sql
    select distinct t from Team t join fetch t.members
    ```
    

## 4. fetch join 을 할 때 생기는 에러가 생기는 3가지 에러 메시지의 원인과 해결 방안

### 4.1. "HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!"

**원인**: 일대다 관계에서 fetch join과 함께 페이징을 사용할 때 발생합니다. Hibernate는 모든 데이터를 메모리에 로드한 후 페이징을 수행하게 됩니다.

**문제점**:

- 대용량 데이터에서 메모리 부족 발생 가능
- 성능 저하

**해결 방안**:

1. `@BatchSize` 사용: 컬렉션에 배치 사이즈를 지정하여 N+1 문제를 완화
    
    ```java
    
    java
    @Entity
    public class Team {
        @BatchSize(size = 100)
        @OneToMany(mappedBy = "team")
        private List<Member> members;
    }
    
    ```
    
2. 글로벌 배치 설정: hibernate.default_batch_fetch_size 설정
    
    ```
    
    properties
    spring.jpa.properties.hibernate.default_batch_fetch_size=100
    
    ```
    
3. 별도의 쿼리로 분리: 연관 엔티티를 별도 쿼리로 조회

### 4.2. "query specified join fetching, but the owner of the fetched association was not present in the select list"

**원인**: fetch join을 사용하는 엔티티가 select 절에 포함되지 않았을 때 발생합니다. 주로 서브쿼리나 복잡한 JPQL에서 발생합니다.

**예시**:

```java

java
// 잘못된 쿼리
select m.name from Member m join fetch m.team
```

**해결 방안**:

1. select 절에 fetch join의 주인 엔티티를 포함시킵니다.
    
    ```java
    java
    // 올바른 쿼리
    select m from Member m join fetch m.team
    ```
    
2. 필요한 경우 DTO로 변환:
    
    ```java
    java
    select new com.example.MemberDto(m.name, t.name)
    from Member m join m.team t
    ```
    

### 4.3. "org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags"

**원인**: 하나의 쿼리에서 둘 이상의 컬렉션(bag)을 fetch join하려고 할 때 발생합니다. Hibernate는 카테시안 곱으로 인한 데이터 폭발을 방지하기 위해 이를 제한합니다.

**예시**:

```java
java
// 에러 발생 쿼리
select t from Team t
join fetch t.members
join fetch t.coaches
```

**해결 방안**:

1. 컬렉션 타입을 `List`에서 `Set`으로 변경 (중복 제거):
    
    ```java
    java
    @Entity
    public class Team {
        @OneToMany(mappedBy = "team")
        private Set<Member> members;// List 대신 Set 사용
    }
    ```
    
2. 하나의 컬렉션만 fetch join하고 다른 컬렉션은 `@BatchSize` 적용:
    
    ```java
    java
    // 하나만 fetch join
    select t from Team t join fetch t.members
    ```
    
    ```java
    java
    @Entity
    public class Team {
        @OneToMany(mappedBy = "team")
        private List<Member> members;
    
        @BatchSize(size = 100)
        @OneToMany(mappedBy = "team")
        private List<Coach> coaches;
    }
    ```
    
3. 별도의 쿼리로 분리하여 각각 조회

이러한 문제들은 JPA와 Hibernate의 내부 동작을 이해하고 적절한 패턴을 적용함으로써 해결할 수 있습니다. 특히 대용량 데이터를 다룰 때는 성능 최적화를 위해 이런 개념들을 잘 이해하는 것이 중요합니다.
