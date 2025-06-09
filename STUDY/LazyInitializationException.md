## ⚠️ LazyInitializationException 관련 에러 정리

### ❗ 에러 메시지

```
HttpMessageNotWritableException: 
Could not write JSON: could not initialize proxy 
[com.ssafy.daangn.domain.User#1] - no Session
```

### 📌 발생 시점

Spring Boot API 응답 과정에서 **엔티티 내부에 선언된 Lazy 필드(User 등)** 를 JSON 직렬화하려고 할 때
**이미 Hibernate 세션이 종료되어 LAZY 필드 초기화가 불가능한 경우** 발생함

### 📎 주요 원인

* JPA 관계 필드가 `@ManyToOne(fetch = FetchType.LAZY)` 등으로 설정되어 있음
* 컨트롤러에서 엔티티를 그대로 반환하여 Jackson이 직렬화 시도
* Lazy 필드(`user`, `category`, ...)는 프록시 상태이며, 세션 종료 후 접근되어 오류 발생

---

### ✅ 해결 방법

#### 1. **DTO로 변환하여 응답 (가장 추천)**

엔티티를 직접 리턴하지 않고, 필요한 필드만 포함하는 DTO 클래스를 만들어 반환

```java
public SaleResponseDto getSale(Long id) {
    Sale sale = saleRepository.findById(id).orElseThrow(...);
    return new SaleResponseDto(sale); // user.getNickname() 등 필요한 값만 추출
}
```

#### 2. **Fetch Join으로 Lazy 필드 강제 로딩**

```java
@Query("SELECT s FROM Sale s JOIN FETCH s.user WHERE s.no = :no")
Optional<Sale> findWithUserByNo(@Param("no") Long no);
```

#### 3. (비추천) Jackson 설정을 통해 프록시 직렬화 무시

```yaml
spring:
  jackson:
    serialization:
      FAIL_ON_EMPTY_BEANS: false
```

> ※ 일부 필드가 null 또는 빈 객체로 직렬화될 수 있어 유지보수 시 예기치 않은 버그 발생 가능

---

### 🔐 Best Practice 요약

| 항목        | 권장 방식                            |
| --------- | -------------------------------- |
| API 응답 객체 | DTO 객체로 변환                       |
| 지연 로딩 필드  | 필요한 경우 Fetch Join 또는 DTO 변환 시 접근 |
| 엔티티 직접 반환 | ❌ 금지 (Lazy 로딩 충돌 위험)             |

---

필요하다면 이 내용을 `docs/error-guide.md` 같은 문서로 따로 분리해두는 것도 좋아요.
원하시면 마크다운 버전으로 바로 복사 가능한 `README` 예시도 드릴게요.
