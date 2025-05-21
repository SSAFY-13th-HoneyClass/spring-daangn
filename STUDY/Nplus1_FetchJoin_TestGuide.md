# 🔄 Fetch Join vs N+1 문제 실험 테스트

이 테스트 코드는 JPA의 **지연 로딩(LAZY)** 에서 발생하는 **N+1 문제**를 재현하고,  
`JOIN FETCH`를 사용해 이를 해결하는 예제를 비교 실험할 수 있도록 구성되어 있습니다.

---

## ✅ 사전 조건

- `Sale` 엔티티는 `User`, `Category`, `SaleStatus`를 `@ManyToOne(fetch = LAZY)`로 가지고 있어야 합니다.
- Spring Boot 프로젝트에 `spring.jpa.show-sql=true` 또는 `logging.level.org.hibernate.SQL=DEBUG`가 설정되어 있어야 합니다.

---

## 🧪 테스트 코드 구성

```java
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class Nplus1Test {

    @Autowired SaleRepository saleRepository;
    @Autowired UserRepository userRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired SaleStatusRepository saleStatusRepository;

    @BeforeAll
    void insertDummyData() {
        Category category = categoryRepository.save(new Category("DIGITAL", "디지털 기기"));
        SaleStatus status = saleStatusRepository.save(new SaleStatus("ON_SALE", "판매중"));

        for (int i = 1; i <= 10; i++) {
            User user = userRepository.save(User.builder()
                .id("user" + i)
                .name("이름" + i)
                .nickname("닉네임" + i)
                .password("1234")
                .email("user" + i + "@example.com")
                .temperature(36.5)
                .build());

            Sale sale = Sale.builder()
                .user(user)
                .category(category)
                .status(status)
                .title("상품 " + i)
                .content("설명 " + i)
                .price(10000L + i)
                .discount(0.1)
                .thumbnail("url")
                .isPriceSuggestible(true)
                .viewCount(0)
                .likeCount(0)
                .chatCount(0)
                .latitude(37.5)
                .longitude(127.0)
                .address("서울")
                .addressDetail("101동")
                .build();

            saleRepository.save(sale);
        }
    }

    @Test
    void problem_Nplus1() {
        System.out.println("🔴 N+1 발생");
        List<Sale> sales = saleRepository.findAll();
        for (Sale sale : sales) {
            System.out.println(sale.getUser().getNickname()); // N+1 발생 지점
        }
    }

    @Test
    void solution_JoinFetch() {
        System.out.println("🟢 Fetch Join 사용");
        List<Sale> sales = saleRepository.findAllWithUserCategoryStatus();
        for (Sale sale : sales) {
            System.out.println(sale.getUser().getNickname()); // 추가 쿼리 없음
        }
    }
}
```

---

## 🔍 Repository

```java
@Query("""
    SELECT DISTINCT s FROM Sale s
    JOIN FETCH s.user
    JOIN FETCH s.category
    JOIN FETCH s.status
""")
List<Sale> findAllWithUserCategoryStatus();
```

---

## ✅ 기대 결과

| 테스트 | 설명 | 쿼리 수 |
|--------|------|---------|
| problem_Nplus1 | 단순 `findAll()` + LAZY 접근 | 1 (Sale) + 10 (User) + 10 (Category) + 10 (Status) = 31 |
| solution_JoinFetch | `JOIN FETCH` + `DISTINCT` | 단 1번의 쿼리로 모두 해결 ✅ |

---

> 로그를 통해 쿼리 수가 줄어드는 걸 직접 확인하며,  
> JPA의 Fetch 전략에 대한 이해를 높여보세요 💡
