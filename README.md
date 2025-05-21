# 지난주 보완 및 서비스 작성

## 리펙토링

### 도메인 주도 설계(DDD) 스타일
- 도메인별로 기능을 분리하여 유지보수가 용이한 MSA 스타일의 구조 도입 및 계층별 테스트 구현

![image](https://github.com/user-attachments/assets/471dc02c-b6c8-456d-ae41-9a7b26754d2f)

### Lombok 사용
- @Builder, @NoArgsConstructor, @Getter 등을 통해 코드가 매우 간결해지고 생산성이 눈에 띄게 향상

![image](https://github.com/user-attachments/assets/f47e613a-825f-43a1-b97c-8538fa7d432b)


### User, Post 예약어
- User, Post는 예약어로 사용될 가능성이 높기 때문에 되도록이면 사용하지 않는 것을 추천
- 따라서 User → Member, Post → Board로 리펙토링 진행

# 테스트

## 테스트 목적

- Repository 계층의 JPA 연관관계 매핑, 쿼리 메서드 유효성, N+1 문제 여부 확인
- Service 계층의 비즈니스 로직 흐름 및 유효성 검증
- DTO ↔ Entity 변환 및 예외 처리 동작 확인

![image](https://github.com/user-attachments/assets/5ef8a670-3b45-46c8-b3dc-979a6f971ab9)


## 테스트 구성 방식

| 계층       | 테스트 방법                                     | 설명                                |
| ---------- | ----------------------------------------------- | ----------------------------------- |
| Repository | `@DataJpaTest`                                  | H2 DB를 이용한 슬라이스 테스트      |
| Service    | `@ExtendWith(MockitoExtension.class)` + Mockito | Mock 객체를 활용한 순수 단위 테스트 |

## 테스트 항목

### Repository 테스트

| 도메인        | 검증 항목                                                       |
| ------------- | --------------------------------------------------------------- |
| Board         | `isDeleted` 필터링, 제목 키워드 검색, Lazy 연관관계 접근        |
| Comment       | 부모-자식 연관관계, 대댓글 조회, N+1 의심 영역 확인             |
| Member        | 이메일 중복 확인, 삭제된 회원 제외 쿼리                         |
| BoardPhoto    | 게시글 ID 기준 정렬 조회, Board 연관관계 매핑                   |
| Favorite      | 복합키(member + board) 조회 및 삭제, 중복 좋아요 방지           |
| DirectMessage | 수신자 기준 조회, 송수신자 조합 조회, 기본 필드(null 방지) 확인 |

### 게시글 내에서 부모 없는 댓글만 조회할 때
![image](https://github.com/user-attachments/assets/7fd6a9a3-c73a-44b6-b61c-a1460f5850ef)

### 기존 코드 테스트

![image](https://github.com/user-attachments/assets/db3d8e34-2594-4109-a06d-20fba6073dde)

> [!NOTE]
>
> `em.clear()` 호출 후 Lazy 연관 접근 → N+1 발생 여부 확인 가능
> 

```java
List<Comment> comments = commentRepository.findByBoard_BoardIdAndIsDeletedFalse(1L);
for (Comment c : comments) {
    // 연관된 Member를 접근 → 쿼리 추가 발생
    // N+1 : 부모 엔티티 1개 조회 시 자식 엔티티를 N번 쿼리
    System.out.println(c.getMember().getEmail());
}
```
- findByBoard_BoardIdAndIsDeletedFalse() → 댓글 목록 쿼리 1회 실행
- 루프 안에서 getMember() 호출 → 댓글 수만큼 select * from members where member_id=? 발생
  - 즉, 1 (댓글 목록) + N (각 댓글의 멤버 조회)

### N+1 탐지용 테스트 코드
```java
@Test
@DisplayName("N+1 테스트용 - 댓글 조회 시 member 또는 board에 대한 join 필요성 확인")
void checkNPlusOneIssue() {
    List<Comment> comments = commentRepository.findByBoard_BoardIdAndIsDeletedFalse(1L);
    for (Comment c : comments) {
        // member는 LAZY 이므로 이 접근 시 개별 쿼리 발생 (N+1 문제 유발 가능)
        System.out.println(c.getMember().getEmail());
    }
}
```

### 해결 방법 : JPQL Fetch Join
```java
@Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.board.boardId = :boardId AND c.isDeleted = false")
List<Comment> findWithMemberByBoardId(@Param("boardId") Long boardId);
```

### 문제 해결 테스트

![image](https://github.com/user-attachments/assets/3b34ee30-2118-4852-a92a-0ca5b070275d)

---

### Service 테스트

| 도메인        | 검증 항목                                                         |
| ------------- | ----------------------------------------------------------------- |
| Board         | 게시글 생성 (Member 존재 확인), DTO 변환 흐름                     |
| Comment       | 댓글 생성 (부모/자식 구분), Board/Member 유효성 검증              |
| Member        | 이메일 중복 확인, 회원 저장 정상 흐름                             |
| BoardPhoto    | 사진 추가 시 Board 존재 여부 확인, 사진 저장 및 DTO 응답 검증     |
| Favorite      | 좋아요 등록 시 중복 확인, 취소 시 정확한 삭제 동작                |
| DirectMessage | 메시지 전송 시 Sender/Receiver 존재 확인, 메시지 리스트 반환 검증 |

> [!CAUTION]
>
> Service 테스트는 순수 단위 테스트로 Repository 동작 자체는 가정(mock 처리)
> 
> `Optional.orElseThrow()`를 사용하는 경우 예외 케이스도 반드시 별도 테스트 필요
> 
> `@Builder.Default`나 Builder 누락 필드로 인한 오류 (`isDeleted`, `isRead`) 주의

> [!NOTE]
> @Mock 객체는 실제 DB가 아닌 stub/mock이므로 정확한 행동 설정이 중요
> 
> when(...).thenReturn(...) 구문으로 원하는 응답을 강제 지정해야 함
> 
> verify(...) 구문으로 Repository가 실제 호출되었는지 확인할 것
> 
> 예외 발생 조건 (orElseThrow)도 반드시 별도 테스트로 검증 필요

# 정리

### JPA 연관관계 테스트의 필요성
- 단순 CRUD 테스트가 아닌, 연관관계를 고려한 테스트(N+1, FetchType 등)가 실제 운영 환경의 성능 이슈를 미리 예방할 수 있다는 점을 배웠다.
- @DataJpaTest로 메모리 DB 기반의 경량 테스트가 매우 유용하다.

> [!Important]
> 
> Hibernate Dialect 지정 실패
> 
> → 테스트용 설정 분리 (application-test.properties)
> 
> 그리고 테스트 클래스에 명시적으로 어노테이션 적용
> 
> `@ActiveProfiles("test")`
