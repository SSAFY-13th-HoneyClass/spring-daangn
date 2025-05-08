# 3️⃣ (옵션) JPA 관련 문제 해결

## 1. 어떻게 data jpa는 interface만으로도 함수가 구현이 되는가?

### ✅ 핵심 개념: **프록시(Proxy) 기반의 구현 자동 생성**

Spring Data JPA는 내부적으로

> 인터페이스만 정의하면,
> 런타임에 그 인터페이스의 구현체(프록시 클래스)를 자동 생성해서
> 스프링 컨테이너에 **빈으로 등록**해줘.

---

### ✅ 예시

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);  // 구현 없어도 자동 동작
}
```

### → 실제로는 실행 시점에 아래와 비슷한 구현체가 자동 생성됨:

```java
@Component
public class UserRepositoryImpl implements UserRepository {
    public Optional<User> findByEmail(String email) {
        // 내부적으로 JPQL: SELECT u FROM User u WHERE u.email = :email
    }
}
```

---

### ✅ 어떻게 가능한가?

1. **스프링 부트가 실행될 때** `@EnableJpaRepositories`가 동작
2. `UserRepository` 인터페이스를 스캔
3. **프록시 클래스(UserRepository\$\$EnhancerBySpringCGLIB...)** 생성
4. 쿼리 메서드(`findByEmail`) 이름을 파싱 → JPQL 쿼리 생성
5. `EntityManager`를 사용해서 실행

---

### ✅ 정리

> Spring Data JPA는 인터페이스 이름과 메서드 이름을 기반으로
> **런타임에 프록시 구현체를 자동 생성**해서
> 우리가 굳이 직접 구현하지 않아도 동작하게 해주는 기술이다.

## 3. fetch join(N+1) 할 때 distinct를 안하면 생길 수 있는 문제

### ❗ 개념

- LAZY 로딩된 연관 객체를 **루프 내에서 접근할 때** 발생
- 첫 번째 쿼리로 N개의 부모 엔티티를 불러온 뒤,
  각각의 자식 엔티티를 N번 추가로 조회함 → **총 N+1개의 쿼리 실행**
- N이 커지면 db에 쿼리를 많이 날려야 할 수 있기에 오히려 단점이 될 수 있음

### ❗ 예시

```java
List<Member> members = memberRepository.findAll(); // 1개의 쿼리
for (Member m : members) {
    System.out.println(m.getTeam().getName()); // N개의 추가 쿼리 발생
}
```

### ✅ 해결 방법

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

<br/><br/><br/><br/><br/><br/><br/>
[다 하지 못한 추가 질문ㅠㅜ]

2. data jpa를 찾다보면 SimpleJpaRepository에서 entity manager를 생성자 주입을 통해서 주입 받는다. 근데 싱글톤 객체는 한번만 할당을 받는데, 한번 연결 때 마다 생성이 되는 entity manager를 생성자 주입을 통해서 받는 것은 수상하지 않는가? 어떻게 되는 것일까? 한번 알아보자

3. fetch join 을 할 때 생기는 에러가 생기는 3가지 에러 메시지의 원인과 해결 방안
   1. `HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!`
   1. `query specified join fetching, but the owner of the fetched association was not present in the select list`
   1. `org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags`
