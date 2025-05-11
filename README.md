## 2주차 스터디
### DB 모델링 및 JPA

<br>

#### 1. 당근마켓 DB 모델링

#### 당근마켓을 직접 체험해보며 어떤 기능이 있는지 확인

- 로그인 및 회원가입

- 게시글 작성 및 조회

- 게시글에 내가 판매할 물품 작성

- 내가 사는 동네 근처에 게시글 확인

- 게시글 물품이 판매되었는지 여부 확인

- 내가 원하는 물품에 좋아요 누르기

- 구매하려고 한다면 DM 보내기

- 내가 구매한 물품, 판매한 물품 리스트로 확인하기

<br>

#### 위에서 살펴본 기능을 중심으로 DB 모델링 및 ERD 작성

![alt text](/image/image.png)

- 좋아요 테이블을 회원과 게시글 사이에서 연결

> why?
> - 좋아요를 누른 회원 및 게시글을 기억하여, 좋아요 취소가 가능하도록 하기 위해서

<br>

- DM은 게시글과 회원이 1대1로 연결되도록 설정
> why?
> - DM은 게시글을 작성한 판매자와 구매를 희망하는 희망자 간의 1대1로 데이터를 저장하기 위해서

<br>
<br>
<hr>
<br>
<br>

#### 약간 개선 한 내용
1. location 테이블 분리
- 중복되는 내용을 하나의 테이블로 분리
- 위치정보를 사용하기에 더욱 적합할 것으로 예상

![alt text](/image/image-1.png)


<br>

2. DM_Room 테이블 생성
- DM 테이블에 대화 상대 및 대화 내용을 함께 저장하는 것을 방지


![alt text](/image/image-3.png)


<br>
<br>

-> ERD를 처음 그려보니 실제 서비스와 DB 사용이 어떻게 이루어지는지 직관적이지 않아 그리는데 어려움이 많았음

-> 직접 서비스를 개발해보며 DB 사용 과정을 직접 확인한다면, 이후 DB 모델링을 더욱 수월하게 할 수 있을 것으로 예상

<br>
<br>
<hr>
<br>
<br>

#### Repository 단위 테스트
- 테이블 생성 성공

![alt text](/image/image-4.png)

<br>

- select, insert 등 SQL문 테스트 성공

![alt text](/image/image-5.png)

<br>

> JPA를 사용하는 이유?
> - 직접 SQL문을 작성하지 않아도, 특정 규칙대로 작성하면 SQL문이 실행된다!

<br>

- repository 인터페이스에 메서드 생성

![alt text](/image/image-6.png)

<br>

- 각 메서드 테스트

![alt text](/image/image-7.png)

<br>

- 테스트 성공

![alt text](/image/image-8.png)


<br>

-> JPA를 사용하면 간단한 SQL 문은 직접 만들지 않아도 알아서 생성되는구나!

-> JPA 쿼리 메서드 생성 규칙에 대해 공부하면 사용하기에 더욱 편리하겠다!



ex) 메서드 이름에

- findBy : 조건 조회
- countBy : 조건 개수
- deleteBy : 조건 삭제
- GreaterThan : >
- LessThanEqual : <=
- IsNull 

등등.....


<br>
<br>
<hr>
<br>
<br>

#### 3. JAP 관련 문제

#### 1) data jpa는 interface 만으로 함수가 구현되는 거지?

- 직접 만들어봤지만, 인터페이스에 메서드가 없는데 기본적인 CRUD가 가능했다... 어떻게..?

-> 인터페이스가 Proxy 객체를 생성

-> 이 프록시 객체가 내부적으로 EntityManager를 이용하여 구현체처럼 동작

<br>

> EntityManager ?
- JAP를 통해 데이터베이스와 상호작용하는 중심 객체
- 데이터베이스에 CRUD 작업을 수행하는 객체
- JDBC를 이용해 SQL을 실행

<br>

#### 전체적인 과정
1) Repository에서 메서드 호출
2) Proxy 객체가 메서드를 가로챔
3) 메서드 이름 분석
4) 이름 기반으로 쿼리 생성
5) JPQL 생성 ( Java Persistence Query Language ) 
: JPA가 지원하는 쿼리 방법 중 하나
> - SQL : 테이블을 대상으로 쿼리
> - JPQL : 엔티티 객체를 대상으로 쿼리
6) EntityManager를 통해 실행
7) DB 쿼리 실행
8) 결과를 return Type 객체로 매핑
9) return Type 객체로 리턴


<br>
<br>
<hr>

#### 2) EntityManager는 요청마다 새로 만들어진다. 근데 SimpleJpaRepository는 싱글톤이니까 처음 한 번만 EntityManager를 받는다..?

- EntityManager를 바로 주입하는 게 아니라, Proxy 객체를 주입

- 실제로는 요청마다 새로운 진짜 EntityManager를 생성해서 연결

- SimpleJpaRepository 입장에선 항상 같은 걸 쓰는 것처럼 보이지만, 내부에서는 요청마다 새로운 DB 연결을 사용

<br>
<br>
<hr>

#### 3-X) Fetch Join 이란 무엇인가 

#### fetch join 이란?
- 연관된 엔티티를 즉시 함께 로딩 하도록 명시하는 문법

<br>

> fetch join : 연관 엔티티를 함께 즉시 조회하도록 하는 JPQL 문법

#### fetch join을 사용하면
```
@Query("SELECT m FROM Member m JOIN FETCH m.team")
List<Member> findAllWithTeam();
```

- SELECT m.\*, t.\* FROM member m JOIN team t ON m.team_id = t.id

= 1번의 쿼리로 Member와 Team을 한 번에 조회

<br>
<br>


#### N+1 문제 ?
- JPA에서 1번의 쿼리로 시작했는데, 연관된 엔티티들을 로딩하면서 추가로 N개의 쿼리가 발생하는 문제

```
List<Member> members = memberRepository.findAll();  // 1번 쿼리 (전체 회원 조회)
for (Member m : members) {
    System.out.println(m.getTeam().getName());      // N번 쿼리 (각 회원의 팀 조회)
}
```

- select * from member;  ( 1번 쿼리 실행 )
- select * from team where member_id = ? ( N번 쿼리 실행 )

= N + 1 쿼리 실행

<br>
<br>

#### fetch join을 사용하면
```
@Query("SELECT m FROM Member m JOIN FETCH m.team")
List<Member> findAllWithTeam();
```

- SELECT m.\*, t.\* FROM member m JOIN team t ON m.team_id = t.id

= 1번의 쿼리로 Member와 Team을 한 번에 조회




<br>
<br>
<br>

#### 3) fetch join(N+1) 할 때 distinct를 안하면 생길 수 있는 문제

- 만약 팀과 멤버를 모두 가져오기 위해 fetch join을 사용했을 경우

- 하나의 팀에 멤버가 3명일 경우

```
List<Team> findAllTeamsWithMembers();
```

```
SELECT t.id, t.name, m.id, m.name
FROM team t
JOIN member m ON t.id = m.team_id;
```

가 실행되고, DISTINCT 가 없으므로 결과가 중복되서 출력

```
[Team(id=1, members=[m1, m2, m3]),
 Team(id=1, members=[m1, m2, m3]),
 Team(id=1, members=[m1, m2, m3])]
```

![alt text](/image/image-9.png) ![alt text](/image/image-10.png) ![alt text](/image/image-11.png)

→ 이걸 JPA에서 Team만 select하고 fetch join을 하면 같은 Team 객체가 3번 생성


<br>
<br>
<hr>
<br>
<br>

#### 4) fetch join 을 할 때 생기는 에러가 생기는 3가지 에러 메시지의 원인과 해결 방안

1) HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!

- fetch join 과 페이징을 동시에 사용할 때 발생

- fetch join을 하면 JOIN 결과가 중복된 행을 반환
- 이 상태에서 페이징을 하면 DB 수준에서 원하는 결과 수가 맞지 않게 되고 ( DB 페이징 적용 X )
- Hibernate는 전체 결과를 메모리로 다 읽은 다음 Java 코드로 잘라낸다.
- 성능 저하 발생

> 페이징 : 대량의 데이터를 조회할 때, 특정 범위만 가져오도록 하는 것

```
// findWithMembers 메서드가 fetch Join 이라고 하면
List<Team> teams = teamRepository.findWithMembers(pageRequest);  <- 페이징 불가
```

-> batch fetch를 사용
-> 한번에 batch size 만큼 묶어서 보기
```
@Entity
public class Team {
    @OneToMany(mappedBy = "team")
    @BatchSize(size = 100)
    private List<Member> members;
}
```


<br>
<br>


2) query specified join fetching, but the owner of the fetched association was not present in the select list

- JPQL에서 fetch join을 썼는데, SELECT 절에 주 테이블이 포함되지 않았다

```
SELECT t.name FROM Team t JOIN FETCH t.members
```
> member를 join 해놓고 왜 안쓰냐... 하고 경고

why?

- fetch join 은 연관 객체를 함께 가져오기 위한 목적!!

<br>
<br>

3)  MultipleBagFetchException: cannot simultaneously fetch multiple bags

- 두 개 이상의 @OneToMany List 를 동시에 fetch join 하려고 할 때 발생

```
SELECT t FROM Team t JOIN FETCH t.members JOIN FETCH t.projects
```

- Hibernate 는 fetch join 할 때, 중복 데이터가 생겨도 그냥 반환

- 두 개 이상의 List 를 fetch join 하면 Cartesian Product가 생기고 복구 불가...

> 복구 불가?
- Hibernate는 Cartesian Product 로 생성된 결과로부터 각각 엔티티를 정확하게 분리해서 다시 List에 넣어야 한다

- But, 중복을 제거할 기준이 없고, 순서 보장도 불가능 하다

= 복구 불가능

-> 하나는 fetch join, 하나는 batch fetching 사용
