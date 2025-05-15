# 서비스 코드 작성

## 리펙토링

> [!NOTE]
> 
> 리펙토링: 도메인 주도 설계(DDD) 스타일
> 
> 도메인별로 기능을 분리하여 유지보수가 용이한 MSA 스타일의 구조 도입 및 계층별 테스트 구현

> [!NOTE]
> 
> 리펙토링: Lombok 사용
> 
> @Builder, @NoArgsConstructor, @Getter 등을 통해 코드가 매우 간결해지고 생산성이 눈에 띄게 향상

> [!Important]
> 
> 리펙토링: User, Post 예약어
> 
> User, Post는 예약어로 사용될 가능성이 높기 때문에 되도록이면 사용하지 않는 것을 추천
> 
> 따라서 User → Member, Post → Board로 리펙토링 진행

# 테스트

## 테스트 목적

- Repository 계층의 JPA 연관관계 매핑, 쿼리 메서드 유효성, N+1 문제 여부 확인
- Service 계층의 비즈니스 로직 흐름 및 유효성 검증
- DTO ↔ Entity 변환 및 예외 처리 동작 확인

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

> [!NOTE]
>
> `em.clear()` 호출 후 Lazy 연관 접근 → N+1 발생 여부 확인 가능
> 
> 테스트용 데이터는 `@BeforeEach` 또는 테스트 메서드 내부에서 명확히 설정
> 
> `@DataJpaTest`는 자동 롤백되므로 테스트 간 독립성이 보장됨

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

> [!NOTE]
> 
> JPA 연관관계 테스트의 필요성
> 
> 단순 CRUD 테스트가 아닌, 연관관계를 고려한 테스트(N+1, FetchType 등)가 실제 운영 환경의 성능 이슈를 미리 예방할 수 있다는 점을 배웠다.
> 
> @DataJpaTest로 메모리 DB 기반의 경량 테스트가 매우 유용하다.

> [!Important]
> 
> Hibernate Dialect 지정 실패
> 
> → 테스트용 설정 분리 (application-test.properties)
> 
> 그리고 테스트 클래스에 명시적으로 어노테이션 적용
> 
> `@ActiveProfiles("test")`
