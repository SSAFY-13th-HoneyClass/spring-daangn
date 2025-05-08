# 2주차 JPA 기본 정리

## 0. JPA가 하는 일 & 기본 정리

## ✅ JPA의 주요 역할

| 기능             | JPA가 해주는 일               | 설명                                                               |
| ---------------- | ----------------------------- | ------------------------------------------------------------------ |
| 테이블 생성      | **DDL 자동 생성**             | `@Entity` 클래스 기반으로 테이블 만들어줌 (옵션 설정 필요)         |
| 참조관계 관리    | **외래 키(FK) 매핑**          | `@ManyToOne`, `@OneToMany` 등으로 참조관계 인식 & FK 생성          |
| 쿼리 생략        | **자동 SQL 생성**             | `persist()`, `find()`, `remove()` 등으로 쿼리 직접 작성 안 해도 됨 |
| 지연/즉시 로딩   | **Fetch 전략 관리**           | 필요 시점에만 쿼리 실행 (지연로딩), 한 번에 JOIN (즉시로딩)        |
| 캐시 & 변경 감지 | **1차 캐시 & dirty checking** | 트랜잭션 내 변경된 값 자동 감지해서 UPDATE 수행                    |

---

## ✅ JPA vs Hibernate

- JPA는 **자바 진영의 표준 ORM 인터페이스**
- Hibernate는 **JPA 구현체** 중 하나 (가장 많이 쓰임)
- 즉, 우리가 실제로 쓰는 기능은 대부분 Hibernate가 내부에서 동작시킴

---

## ✅ 테이블 자동 생성은 옵션임

```properties
# application.properties (또는 yml)
spring.jpa.hibernate.ddl-auto=update
```

| 옵션          | 설명                                              |
| ------------- | ------------------------------------------------- |
| `none`        | 아무 작업 안 함 (기존 테이블 사용)                |
| `create`      | 애플리케이션 시작 시 테이블 새로 생성 (기존 삭제) |
| `create-drop` | 시작 시 생성, 종료 시 삭제                        |
| `update`      | 변경된 엔티티에 맞게 테이블 수정                  |
| `validate`    | 엔티티와 테이블 구조 비교만 (불일치 시 오류)      |

> 실무에서는 `update`나 `validate`만 쓰고,  
> `create`, `create-drop`은 개발 환경에서만 사용해.

---

## ✅ 요약

- 엔티티 기반으로 테이블 & 관계를 만드는 건 **JPA가 ORM으로서 자동으로 처리**해주는 핵심 기능
- Hibernate는 JPA의 구현체로, 실제 SQL 생성 등 실무 동작을 담당
- 개발 생산성과 유지보수성을 크게 높여주는 도구이지만, **동작 원리를 이해하고 있어야 문제를 피할 수 있음**

---

## 1. 식별자 생성 전략

- **Long형 사용**

  - `int`보다 넓은 범위
  - DB의 `BIGINT`와 자연스럽게 매핑됨

- **대체키 (Surrogate Key)**

  - 비즈니스 의미와 무관한 인공 키
  - 변경 위험 없음
  - 외래키 참조에 유리함

- **키 생성 전략**
  `@GeneratedValue(strategy = GenerationType.XXX)`  
  사용 가능한 전략은 아래와 같음:

  | 전략       | 설명                       | 사용 예               |
  | ---------- | -------------------------- | --------------------- |
  | `AUTO`     | JPA가 DB에 맞게 자동 선택  | 개발 초기             |
  | `IDENTITY` | DB의 `auto_increment` 사용 | MySQL 등              |
  | `SEQUENCE` | DB 시퀀스 객체 사용        | PostgreSQL, Oracle    |
  | `TABLE`    | 별도 테이블에서 PK 값 관리 | 범용 DB에서 사용 가능 |

  ✅ 실무에선 보통 **Long형 + 대체키 + `IDENTITY`** 조합이 자주 사용됨.

---

## 2. 지연 로딩 vs 즉시 로딩

```java
@Entity
public class Member {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // EAGER로 바꾸면 즉시 로딩
    private Team team;
}
```

### ✅ 차이점

| 항목      | 지연 로딩 (LAZY)                       | 즉시 로딩 (EAGER)        |
| --------- | -------------------------------------- | ------------------------ |
| 로딩 시점 | 실제 접근할 때 쿼리 발생               | 엔티티 로드 시 즉시 JOIN |
| 성능      | 쿼리 최소화 가능                       | 항상 JOIN → 무거움       |
| 제어      | fetch join으로 직접 제어 가능          | 자동 JOIN → 튜닝 어려움  |
| 주의점    | 트랜잭션 밖에서 Lazy 접근 시 예외 발생 | 쿼리 복잡도 증가 가능    |

### ✅ 지연 로딩 장점

- 꼭 필요한 시점에만 조회 → **불필요한 JOIN 방지**
- 성능 최적화에 유리
- fetch join을 이용하면 **제어된 즉시 조회도 가능**

### ✅ 지연 로딩 시 발생 가능한 문제

#### 1. N + 1 문제

❗ 개념

- LAZY 로딩된 연관 객체를 **루프 내에서 접근할 때** 발생
- 첫 번째 쿼리로 N개의 부모 엔티티를 불러온 뒤,
  각각의 자식 엔티티를 N번 추가로 조회함 → **총 N+1개의 쿼리 실행**
- N이 커지면 db에 쿼리를 많이 날려야 할 수 있기에 오히려 단점이 될 수 있음

❗ 예시

```java
List<Member> members = memberRepository.findAll(); // 1개의 쿼리
for (Member m : members) {
    System.out.println(m.getTeam().getName()); // N개의 추가 쿼리 발생
}
```

#### ✅ 해결 방법

- `fetch join` 사용: join뒤에 fetch가 붙으면 지연로딩 설정 무시하고 걍 즉시로딩 하라는 뜻
  ```java
  @Query("SELECT m FROM Member m JOIN FETCH m.team")
  List<Member> findAllWithTeam();
  ```
- `@EntityGraph` 사용 (Spring Data JPA):
  ```java
  @EntityGraph(attributePaths = {"team"})
  List<Member> findAll();
  ```
- `hibernate.default_batch_fetch_size` 설정 (배치 로딩)

### ✅ 즉시 로딩 시 발생 가능한 문제

#### 2. 순환 참조(무한 JOIN) 주의

- 즉시 로딩(EAGER)을 양방향 관계에 다 적용하면  
  JPA가 자동으로 JOIN을 생성하는 과정에서 **무한 루프처럼 JOIN 시도**
- 특히 직렬화(JSON 변환 등) 시 StackOverflow 발생 가능성 있음

✅ 해결 방법

- 양방향 중 한 쪽만 EAGER 사용하거나, LAZY 기본 유지
- `@JsonIgnore`, `@JsonManagedReference`, `@JsonBackReference` 등으로 직렬화 제어

---

#### ✅ 결론 정리

- 연관관계는 **기본적으로 LAZY**로 설정하자
- 필요한 경우에만 **fetch join으로 최적화**
- N+1 문제와 순환 참조는 별개이며 **각각 명확히 관리해야 함**

#### ✅ 참고사항

❗ 실무에서는 @ManyToOne, @OneToOne도 무조건 LAZY로 명시하는 게 좋다

| 관계 종류     | 기본 fetch 전략 |
| ------------- | --------------- |
| `@ManyToOne`  | **EAGER** 😱    |
| `@OneToOne`   | **EAGER** 😱    |
| `@OneToMany`  | **LAZY** ✅     |
| `@ManyToMany` | **LAZY** ✅     |
