# 📝 설계 및 구현 회고 정리

## 1. 중고거래 상태 등의 값 관리를 Enum 대신 테이블로 처리

- `enum`을 사용할 경우 속성 추가 시 DDL 변경이 필요하고, DBMS마다 enum 처리 방식이 달라 관리가 어렵다.
- 대신 테이블(String PK)로 관리하면 참조 제약 조건을 통해 무결성을 유지할 수 있고, 속성 추가가 유연하다.
- 테이블의 PK를 `Long no`가 아닌 `String`으로 설정한 이유는:

  - `JOIN` 없이도 데이터 전달 시 의미 있는 값을 사용할 수 있어 더 **직관적**임
  - 예: 상태가 `"ON_SALE"`이면, 해당 값을 그대로 클라이언트에 전달 가능

    <br/>

## 2. 중고거래 엔티티의 썸네일 이미지, 채팅 수, 관심 수를 직접 보유

- 중고거래 목록을 조회할 때마다 관련 정보를 함께 보여줘야 하므로 **조인 없이 조회할 수 있도록 반정규화**를 적용
- 성능 향상을 위해 해당 필드를 `Sale` 테이블에 직접 포함함

    <br/>

## 3. BaseEntity를 통한 생성/수정 시간 일괄 관리

- 반복되는 생성/수정 시간을 상속 구조로 공통 처리하여 코드 중복 제거
- `LocalDateTime.now()`는 엔티티 생성 시점 기준이라 DB 반영 시점과 차이가 발생할 수 있음
- 따라서 `@PrePersist` 및 `@PreUpdate`를 통해 DB 반영 시점을 정확하게 기록

```java
@PrePersist
public void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = this.createdAt;
}

@PreUpdate
public void onUpdate() {
    this.updatedAt = LocalDateTime.now();
}
```

- 다른 방법으로 `@CreationTimestamp`와 `@UpdateTimestamp`도 있음 . 차이는 아래와 같음

| 항목            | `@PrePersist`, `@PreUpdate`                              | `@CreationTimestamp`, `@UpdateTimestamp`                    |
| --------------- | -------------------------------------------------------- | ----------------------------------------------------------- |
| **실행 시점**   | 엔티티가 `persist()` 또는 `merge()` 되기 직전 (JPA 콜백) | Hibernate가 `INSERT`, `UPDATE` 쿼리 날리기 직전에 자동 주입 |
| **시간 기준**   | Java 코드 내부의 `now()` 호출 시점                       | Hibernate 내부에서 `now()` 평가 (거의 DB 반영 직전)         |
| **결과 정확도** | 아주 정확 (원하는 로직 추가 가능)                        | 충분히 정확 (실제 DB 반영 직전)                             |
| **장점**        | 로직 확장성, 표준 기반                                   | 간결함, 코드량 감소                                         |
| **단점**        | 코드 늘어남                                              | Hibernate 전용 (@Not portable)                              |

<br/>

## 4. 중고거래 상세 조회 시 Sale ↔ SaleImage 조인 방식 고민

### 💡 가능한 방법

1. **Fetch Join**

   - 장점: 쿼리 1번으로 `Sale` + `SaleImage` 모두 로드
   - 단점: `Sale` 엔티티에 `List<SaleImage>` 필드가 필요 (양방향 매핑 부담)

2. **LAZY 로딩 + 동일 트랜잭션 내 후속 쿼리**

   - 장점: 연관관계 유지는 깔끔
   - 단점: 추가 쿼리 발생, 트랜잭션 내 순서 보장 필요

3. **각자 호출 후 서비스에서 조합 (선택한 방식)**
   - 장점: 가장 구현이 간단하고 명시적
   - 단점: 쿼리 2번 발생 (성능은 대부분의 상황에서 무리 없음)

### ✅ 나의 선택: **3번 (서비스단에서 조합)**

- 유지보수, 가독성, 설계 유연성 등을 고려했을 때 가장 부담이 적고 실용적인 방식이라 판단함.
