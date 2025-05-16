# spring-daangn

## 당근마켓 서비스 개요

당근마켓은 중고 거래 플랫폼으로, 사용자들이 동네에서 붕고 물품을 사고팔 수 있도록 합니다.
채팅을 통한 거래, 위치 기반 서비스, 매너 온도 등으로 사용자 간 신뢰 형성을 위한 기능들도 있습니다.

이 과제에서는 다음과 같은 기능들로 데이터 모델링을 했습니다.

- 사용자 정보 관리
- 위치 기반 게시글 작성
- 개시글 찜
- 댓글 작성
- 채팅을 통한 일대일 거래

<br>

---

## ERD 설계

서비스 기능들을 바탕으로 다음과 같은 엔티티들로 ERD를 설계했습니다.

- `USER`  
- `POST`  
- `COMMENT`  
- `POST_LIKE`  
- `CHATROOM`

<br>

### USER (회원)

사용자의 기본 정보를 저장하는 테이블입니다.
role 을 통해 권한 구분이 가능합니다.

- user_id (PK)
- location_id (FK)
- nickname
- email
- password
- role (ENUM: user, admin)
- joined_at
- phone_number
- manner_temperature


### POST (게시글)

게시글은 작성자와 연결되어 거래 상태 정보를 저장합니다.

- post_id (PK)
- user_id (FK)
- title
- content
- created_at
- status (ENUM: 거래중, 예약중, 거래완료)


### COMMENT (댓글)

작성자와 게시글에 대한 외래키를 2개 포함합니다.

- comment_id (PK)
- post_id (FK)
- user_id (FK)
- content
- created_at



### POST_LIKE (찜)

`user_id` 와 `post_id` 조합으로 하나의 게시글에 대해 한 번만 찜할 수 있도록 `UNIQUE` 제약을 걸었습니다.

- like_id (PK)
- user_id (FK)
- post_id (FK)


### CHATROOM (채팅방)

게시글 기준으로 생성되고, 판매자와 구매자 정보를 함께 저장합니다.

`seller_id`와 `buyer_id` 모두 `USER`를 참조하도록 두 개의 외래키를 사용했습니다.

- chatroom_id (PK)
- post_id (FK)
- seller_id (FK)
- buyer_id (FK)
- is_active (BOOLEAN)

<br>

## 고민했던 부분 

- 중복을 방지하기 위해 찜, 댓글, 채팅 기능은 별도의 테이블로 분리했습니다.
- 모든 FK는 비식별 관계로 설계해 확장이 유연하도록 했습니다.
- ENUM 필드로 상태갮을 제한하여 무결성을 확보했습니다.

<br>

---

<br>

## Repository 단위 테스트

### given-when-then 패턴이란?

- given (준비) : 테스트를 수행하기 위한 조건이나 환경을 설정. 테스트할 객체를 생성, 필요한 데이터를 저장하는 등
- when (실행) : 테스트 대상이 되는 행동이나 기능을 수행하는 단계. 메서드를 호출하거나 데이터를 조회하는 작업
- then (검증) : 실행 결과가 예상 결과와 일치하는지 확인.

<br> 


### 테스트 수행 시 발생하는 JPA 쿼리를 로그로 출력

<img width="328" alt="image" src="https://github.com/user-attachments/assets/6f3f58bf-787f-42fb-9dd4-ba012a43abf0" />

<img width="600" alt="image" src="https://github.com/user-attachments/assets/e4dc9af9-562f-4c42-8804-0d1a7c285a3e" />

<img width="348" alt="image" src="https://github.com/user-attachments/assets/cc3b8bb2-049e-489e-b5b8-f6a59a09b215" />

<br>
</br>

---

<br>

### Post.java

```java
package com.example.daangn.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private PostStatus status;
}
```

- `@Table` 어노테이션을 쓰먄, 테이블명 / 스키마 / 인덱스 등을 제어 가능

    ### `@ManyToOne` ?  
  - 여러 개의 post 가 하나의 user와 연결
  - DB 레벨에서 post 테이블에 user_id 칼럼이 생겨
  
    - JPA가 하는 것
      - 저장 : post.setUser(); postRepository.save(post);
      - 기본 lazy라면 실제 user 정보는 Proxy(가짜 객체)로 남아 있다가,
      - p.getUser().getMickname() 같은 순간에 DB에서 SELECT * FROM user WHERE id = ? 실행
      
### FetchType
- Lazy(지연로딩) 
  -  엔티티가 로딩될 때 연관된 엔티티를 즉시 가져오지 않고, 
  - 실제 해당 필드에 접근할 때 쿼리가 실행돼 데이터를 가져옴
    

       1 + N 문제 발생 가능성 있음
  - 1개의 쿼리로 데이터를 조회했는데,
  - 연관된 데이터를 가져오느라 쿼리가 N번 추가로 실행되는 문제

<br>

---

### EntityManagerFactory

- EntityManager를 생성하는 공장
- `Thread-safe` : 여러 스레드에서 동시에 사용 가능
- `@Entity` 클래스들을 스캔 / DB와 연결 준비
- EntityManager 인스턴스를 생성해주는 역할 

<br>

### EnityManager

- 실제 JPA를 통해서 DB작업을 수행하는 객체
- DB에 쿼리를 날려 / 트랜잭션 관리 / 엔티티 저장.조회.수정.삭제
  
- 트랜잭션마다 새로 생성
- 보통 `1 트랜잭션 단위`로 사용 후 폐기함
- `thread-unsafe` : 스레드마다 별도 사용 필요 

> EntityManager 가 없다면 매번 쿼리를 내가 짜고, 트랜잭션도 수동 처리해야 해 
> 

- JPA를 쓴다면 반드시 EntityManager 를 사용해야하는데 ..
- 명시적으로 쓸 때랑 안 쓸 때의 차이가 뭐냐

<br>

### EntityManager 를 명시적으로 쓰는 경우

언제 ?

- 복잡한 쿼리를 직접 작성할 때
- persist, remove, flush, clear, detach 같은 저수준 동작이 필요할 땨

### EntityManager를 직접 안 쓰는 경우

언제 ?

- Spring Data JPA를 사용해서 리포지토리를 자동 생성할 때
- paRepository 인터페이스만 상속해서 메서드 이름으로 쿼리를 자동 생성할 때


<br>

### Spring Data JPA 내부 구조 

```
PostRepository (interface, @Repository)
↓
SimpleJpaRepository (Spring이 자동 구현)
↓
EntityManager ← 여기서 JPA API 호출
```

- 우리가 직접 em을 안써도 스프링이 자동으로 EntityManager를 가져다가 persist, find, remove 를 다 해줌

### flush() clear() 언제 쓸까 이건

- Persistence Context를 먼저 알아야 해
  - 영속성 컨텍스트란?
  - JPA가 엔티티 객체를 보관하고 변경사항읗 추적하는 `1차 캐시 공간`
  - JPA는 여기에 저장된 객체의 변경을 감지해 나중에 DB에 자동 반영해줌
  - 근데 이 쿼리 실행이 바로 일어나는게 아니라,
  - 트랜잭션 커밋 시점이나 `flush 시점` 에 일어남
  
- flush() 는 뭔데?
  - 영속성 컨텍스트의 변경 내용을 강제로 DB에 반영하는 메서드
  
```
    em.persist(user); // 아직 DB에 INSERT 안됨
    // 현재는 영속성 컨텐스트에만 있음
    em.flush(); // 여기서 INSERT 쿼리 실행됨
 ```

- clear() 는 뭐야?
  - 영속성 컨텍스트를 비워서 모든 관리 객체를 `detach`로 만듦
- 즉, flush()는 DB에 반영만 하고.
- clear()는 JPA의 메모리 캐시 자체를 초기화한다


<br></br>

```java
package com.example.daangn.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLike {
    @Id
    @GeneratedValue
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
```
### PostStatus

```java
package com.example.daangn.domain;

public enum PostStatus {
    ACTIVE, RESERVED, FINISHED
}

```
### PostService.java

```java
package com.example.daangn.service;

import com.example.daangn.domain.Post;
import com.example.daangn.domain.PostStatus;

import java.util.List;

public interface PostService {
    List<Post> getPostsByUserId(Long userId);
    List<Post> getPostsByStatus(PostStatus status);
    List<Post> searchPostsByTitle(String keyword);
}
```
### PostServiceImpl.java 

```java
package com.example.daangn.service;

import com.example.daangn.domain.Post;
import com.example.daangn.domain.PostStatus;
import com.example.daangn.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    @Override
    public List<Post> getPostsByStatus(PostStatus status) {
        return postRepository.findAllByStatus(status);
    }

    @Override
    public List<Post> searchPostsByTitle(String keyword) {
        return postRepository.findByTitleContaining(keyword);
    }
}

```

<br></br>

## 테스트 코드

### 

```java
package com.example.daangn.repository;

import com.example.daangn.domain.Post;
import com.example.daangn.domain.PostStatus;
import com.example.daangn.domain.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("User와 연관된 Post 조회 시 1+N 문제 확인")
    void testOnePlusOne() {
        // given
        User user = userRepository.save(User.builder().name("홍정인").build());
        // 하나의 사용자를 지정하고
        // 이 사용자가 작성한 게시글 10개 저장함
        // 모든 Post는 하나의 user를 참조함
        for (int i = 0; i < 10; i++) { // 1 + N 문제 생길 수 있음
            postRepository.save(Post.builder()
                    .user(user)
                    .title("제목" + i)
                    .content("내용" + i)
                    .status(PostStatus.RESERVED)
                    .build());
        }
        // flush() 로 DB에 쿼리를 날림
        em.flush();
        // clear() 로 영속성 컨텍스트 비워서 1차 캐시 초기화 함
        // -> 이렇게 하면 finadAll() 이후 getUser()를 호출할 때 실제 DB쿼리가 발생함
        em.clear();

        // when
        List<Post> posts = postRepository.findAll();

        // then
        for (Post post : posts) {
            System.out.println(post.getUser().getName());
        }
        
        assertThat(posts).hasSize(10);
    }
}
```
<br>

- 저번에 공부했던 1 + N 문제가 POST 랑 USER 데이터를 같이 조회할 때 발생하는지 테스트
- POST 는 USER 와 `@ManyToOne` 관계
- `fetch = LAZY` 이기 때문에 POST 를 먼저 조회하고 getUer() 를 호출할 때마다
- 추가 쿼리가 발생할 수 있음

<br>

```console 
Hibernate: 
    select
        p1_0.post_id,
        p1_0.content,
        p1_0.created_at,
        p1_0.status,
        p1_0.title,
        p1_0.user_id 
    from
        post p1_0
2025-05-16T10:45:36.079+09:00 DEBUG 13783 --- [    Test worker] org.hibernate.SQL                        : 
    select
        u1_0.user_id,
        u1_0.created_at,
        u1_0.email,
        u1_0.name,
        u1_0.password,
        u1_0.role,
        u1_0.updated_at 
    from
        user u1_0 
    where
        u1_0.user_id=?
Hibernate: 
    select
        u1_0.user_id,
        u1_0.created_at,
        u1_0.email,
        u1_0.name,
        u1_0.password,
        u1_0.role,
        u1_0.updated_at 
    from
        user u1_0 
    where
        u1_0.user_id=?
        ...
```

	•	Post는 10개인데, 각 Post의 user를 getUser()로 접근할 때마다 쿼리 1개씩 발생
	•	→ 1 (Post 전체) + N (User N명) → 1+N 문제

이 문제를 해결하기 위해서 `fetch join` 사용함
- 연관된 엔티티를 한 번의 쿼리로 조인해서 Post + User 정보를 한꺼번에 가져옴
- 총 쿼리 수 1개 !


→ `@Query("SELECT p FROM Post p JOIN FETCH p.user")`

<br>


```java
package com.example.daangn.service;

import com.example.daangn.domain.Post;
import com.example.daangn.domain.PostStatus;
import com.example.daangn.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

// Mockito 를 사용할 수 있게 해줌
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    // 가짜 객체 만들어줌 
    // -> PostRepository 의 가짜 구현체 만듦 (DB없이 테스트 가능 )
    @Mock
    private PostRepository postRepository;
    // Mock으로 만든 의존성들을 주입해서 테스트 객체를 자동 생성함
    @InjectMocks
    private PostServiceImpl postService;

    private Post post1;
    private Post post2;

    @BeforeEach
    void setup() {
        post1 = Post.builder().postId(1L).title("p1").status(PostStatus.ACTIVE).build();
        post2 = Post.builder().postId(2L).title("p2").status(PostStatus.ACTIVE).build();
    }

    @Test
    void testGetPostsByStatus() {
        // given
        when(postRepository.findAllByStatus(PostStatus.ACTIVE)).thenReturn(Arrays.asList(post1, post2));

        // when
        List<Post> result = postService.getPostsByStatus(PostStatus.ACTIVE);

        // then
        assertThat(result).hasSize(2);
        verify(postRepository, times(1)).findAllByStatus(PostStatus.ACTIVE);
    }

    @Test
    void testSearchPostsByTitle() {
        // given
        when(postRepository.findByTitleContaining("p1")).thenReturn(List.of(post1));

        // when
        List<Post> result = postService.searchPostsByTitle("p1");

        // then
        assertThat(result.get(0).getTitle()).isEqualTo("p1");
    }
}

```

### Mock

- 실제 DB 사용하지 않고, PostRepository를 mock 객체로 대체
- @InjectMocks 로 mock 객체주입
- 내가 정한 값만 retun







