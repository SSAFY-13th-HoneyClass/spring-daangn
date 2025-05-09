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

![image](https://github.com/user-attachments/assets/f7fba4f3-c1ca-4d2d-bbf0-2e539e96f3d9)


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


### User entity

```java
package com.example.daangn.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    private Long userId;

    private String nickname;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

```

```java
package com.example.daangn.domain;

public enum Role {
    USER, ADMIN
}

```

```java
package com.example.daangn.repository;

import com.example.daangn.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

<br>

```java
package com.example.daangn.repository;

import com.example.daangn.domain.Role;
import com.example.daangn.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 3명 저장")
    void saveUsers() {
        // given
        User user1 = User.builder()
                .nickname("홍정인")
                .email("sss@ss.ss")
                .password("1234")
                .role(Role.ADMIN)
                .build();
        User user2 = User.builder()
                .nickname("이휘")
                .email("hwi@w.w")
                .password("1234")
                .role(Role.USER)
                .build();
        User user3 = User.builder()
                .nickname("이주현")
                .email("math@m.m")
                .password("1234")
                .role(Role.USER)
                .build();
        // when
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        Optional<User> found = userRepository.findByEmail("sss@ss.ss");
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getNickname()).isEqualTo("홍정인");
        assertThat(found.get().getRole()).isEqualTo(Role.ADMIN);
    }
}

```

- given : 3명의 사용자 데이터 생성
- when : 사용자들을 DB에 저장하고 이메일로 조회
- then : 기대한 결과 값과 일치하는지 확인 


### given-when-then 패턴이란?

- given (준비) : 테스트를 수행하기 위한 조건이나 환경을 설정. 테스트할 객체를 생성, 필요한 데이터를 저장하는 등
- when (실행) : 테스트 대상이 되는 행동이나 기능을 수행하는 단계. 메서드를 호출하거나 데이터를 조회하는 작업
- then (검증) : 실행 결과가 예상 결과와 일치하는지 확인.

<br> 


### 테스트 수행 시 발생하는 JPA 쿼리를 로그로 출력

<img width="328" alt="image" src="https://github.com/user-attachments/assets/6f3f58bf-787f-42fb-9dd4-ba012a43abf0" />

<img width="600" alt="image" src="https://github.com/user-attachments/assets/e4dc9af9-562f-4c42-8804-0d1a7c285a3e" />

<img width="348" alt="image" src="https://github.com/user-attachments/assets/cc3b8bb2-049e-489e-b5b8-f6a59a09b215" />






