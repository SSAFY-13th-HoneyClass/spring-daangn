# spring-daangn

# JPA란?
- Java Persistence API

java에서 데이터베이스를 쉽게 다룰 수 있도록 도와주는 기술.
- 직접 SQL을 작성하지 않고 java 코드로 데이터베이스를 다울 수 있게 해줌.

기존 방식
```
String sql = "SELECT * FROM users WHERE id = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setLong(1, id);
ResultSet rs = pstmt.executeQuery();
```
JPA 사용
```
User user = entityManager.find(User.class, id);
```

### 🧱 핵심 용어

| 용어 | 설명 |
|------|------|
| `@Entity` | DB 테이블과 매핑되는 Java 클래스 |
| `EntityManager` | Entity 저장, 조회, 삭제 등을 담당 |
| `@Id` | 테이블의 기본 키(PK)를 의미 |
| `@GeneratedValue` | PK의 자동 증가 설정 |
| `@OneToMany`, `@ManyToOne` | 테이블 간의 관계 설정 (Join 처리) |


### 📌 자주 사용하는 JPA 어노테이션 정리

| 어노테이션 | 설명 |
|------------|------|
| `@Entity` | 클래스가 테이블과 매핑됨 |
| `@Table(name = "table_name")` | 매핑할 테이블명을 지정 (기본은 클래스명) |
| `@Id` | 기본 키(PK)를 지정 |
| `@GeneratedValue(strategy = ...)` | 기본 키 자동 생성 전략 (IDENTITY, SEQUENCE, AUTO 등) |
| `@Column(name = "col_name", nullable = false, unique = true, length = 100)` | 컬럼 이름, 제약조건, 길이 등을 설정 |
| `@Transient` | 해당 필드는 DB에 저장되지 않음 (임시 필드로만 사용) |
| `@Lob` | Large Object: BLOB(CLOB) 데이터를 매핑할 때 사용 |
| `@Enumerated(EnumType.STRING)` | Enum 타입을 DB에 저장할 때 문자열로 저장 |
| `@Temporal(TemporalType.DATE)` | 날짜 타입을 DATE, TIME, TIMESTAMP 중 하나로 매핑 |
| `@Embedded` | 다른 클래스의 필드를 포함시킬 때 사용 (값 타입 객체) |
| `@Embeddable` | @Embedded로 사용될 클래스에 붙임 |

### 📚 관계 매핑 어노테이션 (테이블 간 관계 설정)

| 어노테이션 | 설명 |
|------------|------|
| `@OneToOne` | 1:1 관계 설정 |
| `@OneToMany` | 1:N 관계 설정 (컬렉션) |
| `@ManyToOne` | N:1 관계 설정 (외래 키 가짐) |
| `@ManyToMany` | N:N 관계 설정 |
| `@JoinColumn(name = "fk_name")` | 외래 키(FK) 컬럼명을 지정 |
| `@JoinTable(...)` | N:N 관계에서 조인 테이블을 직접 지정 |
| `mappedBy` | 양방향 관계에서 연관관계의 주인이 아닌 쪽에 사용 |

예제 코드
```
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Transient
    private String tempValue; // DB에 저장되지 않음

    @OneToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
}

```

# 🌱 2주차 미션
## 1️⃣ 당근마켓의 DB를 모델링해요
## 2️⃣ Repository 단위 테스트를 진행해요
## 3️⃣ (옵션) JPA 관련 문제 해결
<br>

# 1️⃣ 당근마켓의 DB를 모델링해요

# 🥕 당근마켓 클론 서비스
당근마켓은 지역 기반 중고 거래 플랫폼으로, 사용자는 자신의 위치 기반으로 중고 물품을 등록하고 거래할 수 있으며, 채팅, 댓글, 좋아요, 매너평가 등의 기능을 통해 신뢰 기반의 거래가 가능하다.

![daangn (1)](https://github.com/user-attachments/assets/e6ebdf49-b539-4e4f-97d2-37bc017fb16b)


# 📦 모델 설명

## 📍 `region`

| Column | Type         | Constraints         | Description |
|--------|--------------|---------------------|-------------|
| id     | INT          | PK, AUTO_INCREMENT  | 지역 ID     |
| name   | VARCHAR(100) | NOT NULL            | 지역명      |

---

## 📁 `category`

| Column | Type         | Constraints         | Description   |
|--------|--------------|---------------------|---------------|
| id     | INT          | PK, AUTO_INCREMENT  | 카테고리 ID   |
| type   | VARCHAR(50)  | NOT NULL            | 카테고리 이름 |

---

## 👤 `user`

| Column      | Type            | Constraints                                | Description        |
|-------------|-----------------|--------------------------------------------|--------------------|
| id          | BIGINT          | PK, AUTO_INCREMENT                         | 사용자 ID          |
| region_id   | INT             | NOT NULL, FK → `region(id)`                | 지역 FK           |
| email       | VARCHAR(100)    | NULLABLE                                   | 이메일             |
| password    | VARCHAR(255)    | NULLABLE                                   | 비밀번호           |
| nickname    | VARCHAR(50)     | NULLABLE                                   | 닉네임             |
| phone       | VARCHAR(20)     | NULLABLE                                   | 전화번호           |
| temperature | DECIMAL(4,2)    | DEFAULT 36.5                               | 매너 온도          |
| create_at   | DATETIME        | DEFAULT CURRENT_TIMESTAMP                  | 생성일자           |
| update_at   | DATETIME        | DEFAULT CURRENT_TIMESTAMP ON UPDATE NOW()  | 수정일자           |
| profile_url | VARCHAR(255)    | NULLABLE                                   | 프로필 이미지 URL  |

---

## 📦 `product`

| Column         | Type           | Constraints                              | Description         |
|----------------|----------------|------------------------------------------|---------------------|
| id             | BIGINT         | PK, AUTO_INCREMENT                       | 상품 ID             |
| seller_id      | BIGINT         | NOT NULL, FK → `user(id)`                | 판매자 FK           |
| category_id    | INT            | NOT NULL, FK → `category(id)`            | 카테고리 FK         |
| region_id      | INT            | NOT NULL, FK → `region(id)`              | 지역 FK             |
| title          | VARCHAR(100)   | NOT NULL                                 | 상품 제목           |
| thumbnail      | VARCHAR(255)   | NOT NULL                                 | 썸네일 URL          |
| description    | TEXT           | NULLABLE                                 | 상품 설명           |
| price          | INT            | NULLABLE                                 | 가격                |
| is_negotiable  | TINYINT(1)     | DEFAULT 0                                | 가격 제안 여부      |
| created_at     | DATETIME       | DEFAULT CURRENT_TIMESTAMP                | 등록 시간           |
| dump_time      | INT            | NULLABLE                                 | 삭제까지 남은 시간 |
| is_reserved    | TINYINT(1)     | NOT NULL, DEFAULT 0                      | 예약 여부           |
| is_completed   | TINYINT(1)     | NOT NULL, DEFAULT 0                      | 거래 완료 여부      |
| chat_count     | BIGINT         | NOT NULL, DEFAULT 0                      | 채팅 수             |
| view_count     | BIGINT         | NOT NULL, DEFAULT 0                      | 조회 수             |
| favorite_count | BIGINT         | NOT NULL, DEFAULT 0                      | 찜 수               |

---

## ❤️ `favorite`

| Column     | Type   | Constraints                        | Description |
|------------|--------|------------------------------------|-------------|
| id         | BIGINT | PK, AUTO_INCREMENT                 | 찜 ID        |
| user_id    | BIGINT | NOT NULL, FK → `user(id)`         | 사용자 FK   |
| product_id | BIGINT | NOT NULL, FK → `product(id)`      | 상품 FK     |

---

## 💬 `comment`

| Column            | Type          | Constraints                   | Description        |
|-------------------|---------------|-------------------------------|--------------------|
| id                | BIGINT        | PK, AUTO_INCREMENT            | 댓글 ID            |
| product_id        | BIGINT        | NOT NULL, FK → `product(id)` | 상품 FK            |
| user_id           | BIGINT        | NOT NULL, FK → `user(id)`    | 작성자 FK          |
| content           | TEXT          | NOT NULL                      | 내용               |
| create_at         | DATETIME      | DEFAULT CURRENT_TIMESTAMP     | 작성 시각          |
| parent_comment_id | BIGINT        | NULLABLE                      | 부모 댓글 ID       |
| child_count       | INT           | DEFAULT 0                     | 자식 댓글 수       |
| level             | INT           | DEFAULT 0                     | 계층 깊이          |
| hierarchy_path    | VARCHAR(255)  | NULLABLE                      | 계층 경로          |

---

## 👍 `manner_detail`

| Column  | Type         | Constraints         | Description  |
|---------|--------------|---------------------|--------------|
| id      | INT          | PK, AUTO_INCREMENT  | 항목 ID      |
| content | VARCHAR(100) | NULLABLE            | 항목 내용    |

---

## ⭐ `manner_rating`

| Column        | Type   | Constraints                         | Description        |
|---------------|--------|-------------------------------------|--------------------|
| id            | BIGINT | PK, AUTO_INCREMENT                  | 평가 ID            |
| rated_user_id | BIGINT | NOT NULL, FK → `user(id)`          | 평가 대상 FK       |
| detail_id     | INT    | NOT NULL, FK → `manner_detail(id)` | 매너 항목 FK       |
| rater_user_id | BIGINT | NOT NULL                            | 평가자 ID          |

---

## 🖼️ `image`

| Column     | Type         | Constraints                   | Description    |
|------------|--------------|-------------------------------|----------------|
| id         | BIGINT       | PK, AUTO_INCREMENT            | 이미지 ID      |
| product_id | BIGINT       | NOT NULL, FK → `product(id)` | 상품 FK        |
| image_url  | VARCHAR(255) | NOT NULL                      | 이미지 URL     |
| order      | INT          | NOT NULL                      | 순서           |

---

## 🗨️ `chat_room`

| Column     | Type   | Constraints                        | Description  |
|------------|--------|------------------------------------|--------------|
| id         | BIGINT | PK, AUTO_INCREMENT                 | 채팅방 ID     |
| buyer_id   | BIGINT | NOT NULL, FK → `user(id)`         | 구매자 FK     |
| seller_id  | BIGINT | NOT NULL, FK → `user(id)`         | 판매자 FK     |
| product_id | BIGINT | NOT NULL, FK → `product(id)`      | 상품 FK       |

---

## ✉️ `message`

| Column       | Type     | Constraints                        | Description       |
|--------------|----------|------------------------------------|-------------------|
| id           | BIGINT   | PK, AUTO_INCREMENT                 | 메시지 ID         |
| chat_room_id | BIGINT   | NOT NULL, FK → `chat_room(id)`    | 채팅방 FK         |
| sender_id    | BIGINT   | NOT NULL                          | 보낸 사용자 ID     |
| message      | TEXT     | NOT NULL                          | 메시지 내용        |
| send_at      | DATETIME | DEFAULT CURRENT_TIMESTAMP         | 보낸 시간          |
| is_read      | TINYINT  | NOT NULL, DEFAULT 0               | 읽음 여부 (0/1)    |


## ❗ 비상 사태
단위테스트가 안된다…

하나씩 다시 해보자

일단 Gradle 이 익숙 하지 않아 조금 찾아봤다.

의존성 관리는 build.gradle 에서 DI , 의존성 주입을 해줘야 한다.

gradle은 뭔데..?

1. 라이브러리 관리
2. 프로젝트 빌드 
3. 테스트, 실행, 패키징 자동화

src/main/resources에 있는 application.properties는 지금 까지 항상 사용해왔던 프로빠리

Spring Boot의 환경 설정 파일이다.

하지만 이번에는 yml 파일로 변경하여 진행 할 것이다.

두개의 차이가 뭘까?

.properties 는 key = value 방식이고

.yml 은 들여쓰기 기반 구조이다.

사실상 같은 기능을 하는것

쁘로빠리의 장점은 상대적으로 명확하다, 하지만 설정이 많아지면 가독성이 떨어지게 된다.

야물의 장점은 더 계층적이고 정돈되어 가독성은 좋지만 들여쓰기 실수 시 에러가 날 수 있다는 점이있다.

제일 위에 생기는 4개의 폴더는 뭐야..?

.gradle : Gradle 빌드 중에 생기는 캐시 파일 저장소 이다.

.idea : IntelliJ 프로젝트 설정 파일들( 공동작업시 .gitignore에 넣는게 일반적)

build : Gradle로 빌드한 결과물이 저장되는 폴더( 실행 가능한 .jar, 클래스파일 등)

gradle : Gradle Wrapper 설정 폴더 (버전 정보 등 포함)

이런파일이 생긴다…

아래 애들은..?

build.gradle : Gradle 빌드 설정 파일 (의존성, 빌드 옵션, 플러그인 등 작성)

settings.gradle : 프로젝트 이름 정의

gradlew, gradlew.bat : Gradle이 설치되어 있지 않아도 실행할 수 있도록 도와주는 실행 스크립트

라고한다..

다음에는 단위테스트를 해보자..

먼저 실제 DB 연결 안하고 간단하게 실행해 보고싶었다.

H2 인메모리 데이터베이스를 사용하면 살제 DB와 연동되는 JPA Repository 단위 테스트를 진행 할 수 있다고 한다.

의존성으로 jpa와 h2database, test 이렇게 3개 추가해주었다.

앗 롬복도 없네.. 추가해준다.

근데 궁금증

Entity랑 Domain이랑 DTO랑 뭐가다른거야?

1. Entity는 DB의 테이블과 1:1 매핑되는 클래스이다. JPA의 @Entity 어노테이션으로 지정..

DB 설계에 충실한 모델로 유지할 것!

1. Domain은 넓은 의미의 “도메인 모델”, 비지니스 개념을 담은 모델, Entity와 같을 수도 있지만 Entity를 숨기고 별도로 관리 할 수 있다.. 실무에서는 Entitu가 Domain역할을 할 때가 많지만 DDD(도메인 주도 설계)에서는 Entity와 Domain을 엄격히 나누기도 한다.

1. DTO (Data Transfer Object) 는 계층간 데이터 전달용 객체이다, Controller , Service , View 사이에서 Entity를 노출하지 않고 DTO를 사용해 안전하고 효율적인 전송이 가능하게 한다. 보통 Entity를 직접 외부에 노출하지 않기 위해 DTO를 따로 정의한다.

이런식으로 폴더를 구성한다고 한다.. 뭐 프로젝트 규모와 팀의 선호도에 따라 달라질 수 있다곤 하니 정답은 없나보다.

```java
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           ├── config/           // 설정 클래스
│   │           ├── controller/       // REST API 컨트롤러
│   │           ├── dto/              // DTO 클래스
│   │           ├── entity/           // JPA 엔티티
│   │           ├── repository/       // JPA 리포지토리
│   │           ├── service/          // 비즈니스 로직
│   │           └── SpringBootBaseArchitectureApplication.java
│   └── resources/
│       ├── application.properties    // 애플리케이션 설정
│       └── static/                   // 정적 리소스
└── test/
    └── java/
        └── com/
            └── example/
                └── ...               // 테스트 클래스

```

```java
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
}

```

간단하게 user 도메인을 하나 만들었다.

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

```

간단하게 레포지토리도 하나만들었다.

근데 뭔지 모르는거 투성이다.

JPARepository를 extends하면 뭐가되나..?

1. 기본 CRUD 메서드 제공 :`save()`, `findById()`, `findAll()`, `deleteById()` 등 자동으로 사용 가능
2. 페이징 및 정렬 : `findAll(Pageable pageable)`, `findAll(Sort sort)` 등 제공
3. 커스텀 메서드 생성 가능 : 메서드 이름만으로 `findByEmail()`처럼 쿼리 메서드 자동 생성
4. 프록시 객체로 구현 클래스 자동 생성 : 개발자가 직접 구현하지 않아도 런타임에 Spring이 프록시로 구현체 제공
5. 트랜잭션 처리 자동화 : 스프링에서 트랜잭션 처리를 간단하게 설정 가능
6. 엔티티 기반 쿼리 작성 용이 : JPQL, @Query 사용가능, QueryDSL과의 통합도 수월

그럼 그뒤에 <User, Long> 은 뭔데?

제테릭 타입으로 JPARepository가 어떤 언티티를 다루고 어떤 타입의 PK를 갖는지 지정해주는 부분이다.

- 타입 안정성 보장 : 잘못된 타입은 컴파일 에러 발생
- User 관련 CRUD 메서들이 자동생성

Optional<User> 하는 이유는 뭔데?

- null이 될 수도 있는 User 객체를 감싸는 그릇, 데이터가 존재하지 않을 수 있기 때문
- User가 있을 수도, 없을 수도 있음을 표현하고 null 처리에 대해 명확하고 안전하다, java8 이후 등장한 기능으로 JPA에서도 적극 활용!!

좋았다..

드디어 테스트 폴더 들어가본다

```java

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void 유저_저장_및_이메일로_조회_테스트() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("테스트");

        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("test@example.com");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("테스트");
    }
}
```

@DataJpaTest 는 뭐냐

Spring Boot에서 JPA 관련 컴포넌트만 골라서 테스트 할 수 있게 해주는 테스트용 어노테이션!!

Repository테스트에 특화된 환경을 빠르게 구성해준다.

### ✅ Spring Boot 테스트 어노테이션 정리

| 어노테이션 | 용도 | 특징 |
| --- | --- | --- |
| `@SpringBootTest` | **통합 테스트**용 전체 애플리케이션 컨텍스트 로딩 | 컨트롤러, 서비스, 리포지토리까지 다 올라옴 |
| `@WebMvcTest` | **컨트롤러 단위 테스트** | 서비스, 리포지토리는 제외. `@Controller`, `@RestController`만 테스트 |
| `@DataJpaTest` | **JPA Repository 단위 테스트** | JPA 관련 빈만 로딩, DB 자동 롤백 |
| `@MockBean` | 테스트 중 필요한 의존 객체를 **Mocking**할 때 사용 | 가짜 Bean을 주입함 |
| `@TestConfiguration` | 테스트 전용 설정 클래스 정의 | `@Configuration`의 테스트 버전 |
| `@AutoConfigureMockMvc` | `MockMvc` 사용 설정 | 실제 서버 없이 Spring MVC 테스트 가능 |
| `@Transactional` | 테스트 메서드 트랜잭션 처리 | 테스트 후 자동 롤백되는 데 유용 |
| `@TestPropertySource` | 테스트용 프로퍼티 설정 적용 | 커스텀 `application-test.yml` 등 사용할 때 |

뭔가 이렇게 많단다.

아니그래서 assertThat(result).isPresent() 이거뭔데

- null 이 아닌 값이 존재하냐? 라는 뜻

그아래는 이제 값 꺼내서 비교해본거지..

근데 비교를 왜 assertEquals()가 아닌 특이한방식으로 헀어?

- AssertJ 방식 : 실제값 기준으로 이 값이 테스트와 같아야 한다, 더 읽기 쉬운 현대식 체이닝 스타일 이라한다.
- 메서드 체이닝, 더 풍부한 표현식, 디버깅 친화적에 가독성이 높다..

그럼 Equals는 뭐가문제야

- 고전적인 JUnit 스타일이지만 간단하고 익숙하긴해

assertThat이 좋긴해보이네

- 근데 (expected, actual) 순서로 순서 중요한데 순서 바뀌면 헷갈림

그럼 결국 코드 읽어보면 실제로 User user에 값 넣고 JPARepository가 자동으로 주는 CRUD 중 save 써서 하나 저장하고

findByEmail 만들어둔거로 result 값 뽑아본다음

assertThat에 null 아니니? 이후 테스트 값 맞니?? 해봤더니

두개다 만족해야 테스트 통과가 나온다..

이제야 이해가간다

그럼 이 save 한 데이터는 실제로 남아있냐??

아니다.. 각 테스트는 트랜잭션 안에서 실행되고, 자동으로 롤백된다!!!

왜냐! @DataJpaTest에는 기본적으로 @Transactional이 포함되어 있기 때문에 테스트가 끝나면 롤백된다.

@Transactional(propagation = Propagation.NOT_SUPPORTED 하면 트랜잭션 끌수도 있다.

처음으로 단위테스트 통과 초록불을 봤다…

위에 한것들이 노트북으로 했는데 안돌아가서 처음부터 깎아본거다.. 처음으로 초록불 떴으니 노트북으로 다시 실행해 봤는데 역시나 안되고 오류를 못찾겠다..

gpt가 해당 위치에 가면 에러 뭔지 찾아 줄수 있단다…

![image](https://github.com/user-attachments/assets/31de2864-f604-448a-854c-c6da0a9fabc0)


근데도 못찾아.. 아래 사진이 해당 html이다 저기가 에러나면 왜터지는지 저장하는 곳이란다

![image](https://github.com/user-attachments/assets/a3fb03cd-348d-4be2-9351-36a6300219d1)


시도 1. 혹시 db 연결이 안되어서그런가? 
`runtimeOnly 'com.h2database:h2'` 

위에서 했듯이 해당 의존성을 추가해서 돌려보았지만… 뺴엑..

시도 2. 그럼 간단한 단위 테스트부터 해볼까?

```jsx
public class SimpleTest {

    @Test
    void testAddition() {
        int result = 1 + 2;
        assertThat(result).isEqualTo(3);
        System.out.println("✅ 단순 테스트 성공");
    }
}

```

이 테스트 코드조차 똑같은 에러코드가 뜬다. 빼에엑…

시도 3. build.gradle이 test 위치 못찾는거 같다고 이거로 위치 찾아주랜다.

```jsx
sourceSets {
	test {
		java {
			srcDirs = ['src/test/java']
		}
	}
}
```

역시나 어림도 없다.

시도 4. build.gradle이 의존성 제대로 안먹을수도 있다고

```jsx
./gradlew clean build --no-build-cache
```

이거로 캐시 날려보라해서 날려봣지만 빼에엑..

시도 5. 

```jsx
//tasks.named('test') {
//	useJUnitPlatform()
//}
test {
	useJUnitPlatform()
}

```

찾아보니 기존의 build.gradle 에서 주석처리한 부분을 아래 코드로 변경하면 된다는 말이있어서 변경해 보았지만

아직도 뺴에엑….

정답 : 설정에 빌드 및 실행값이 default 가 아닌 IntelliJ IDEA로 바꿔서 해결
![image](https://github.com/user-attachments/assets/ff0e0cdb-8a81-40eb-b3cf-483dd48006be)



해당 설정 페이지에서 다음을 사용하여 테스트 실행을 default 값인 Gradle에서 IntelliJ IDEA로 바꿔주니 실행이된다..

이러니 코드를 아무리 고쳐봐야 안되지

## ❗깃 망가짐
.git 파일이 두개생겼다
pull, push 하는 과정에서 뭔가가 꼬였다..
그냥 새로 fork 해서 레포지토리를 다시 파서 해결했다..

근데 out 이라는 처음보는 폴더가 생겼다
저게 뭐냐

🔹 out 폴더란?
Java 프로젝트를 컴파일할 때 컴파일된 .class 파일을 저장하는 디폴트 출력 디렉토리입니다.

build 폴더와는 다르게, Gradle/Maven이 아닌 IntelliJ 자체 빌드 시스템을 사용할 때 생성됩니다.

📁 하위 폴더 설명
production : src/main/java 쪽 코드를 컴파일한 결과물(.class 파일 등)이 저장됩니다.

test : src/test/java 쪽 테스트 코드를 컴파일한 결과물들이 여기에 저장됩니다.

라고 한다



