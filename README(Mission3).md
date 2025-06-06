## 1️⃣ 당근마켓의 서비스 코드를 작성해요
--------------
>[! warning] 들어가기에 앞서...
>모든 domain에 대한 Service를 구현한 것이 아닌 User와 Product에 대한 Service만 구현하였습니다.

### 프로젝트 폴더 구조화
- 기존 repository와 entity만 존재하던 구조에서
    - => domain별 dto, entity, repository, service를 가지도록 변경
- 향후 프로젝트를 위해 config, controlelr, utils 폴더 생성 (아직 기능은 없음)
  ![[Pasted image 20250516204834.png]]

### 변경 사항
> 1. Entity에서 @Data -> @Getter, @Setter 변경 (피드백 반영)
> 2. test용 application-test.yml 작성



## 2️⃣ Repository 계층의 테스트를 진행해요
-----------
### N+1 문제란?
>[! info]  쿼리 호출 하면 또 쿼리를 호출한다?
>1번 혹은 N번의 쿼리를 호출 할 때 해당 Entity와 연결 관계를 갖는 다른 테이블의 데이터도 가져기 위하여 쿼리를 그 만큼 더 날리는 경우를 의미합니다.

>[! warning] 이에 따른 문제점
>내가 원하지 않던 데이터까지 다 불러오므로 불필요한 데이터를 가지게 된다.
>뿐만 아니라 내가 쿼리를 호출한 만큼 또 다른 쿼리를 한 번 더 날리게 되므로 2배의 쿼리를 날려 시간적 손해를 보게된다.

### 문제 발생 예시
>[!info] PostRepository와 UserRepository 사용

- 테스트 코드
1. 사전 작업 : 테스트용 사용자 2명 생성
```Java
@BeforeEach  
public void setup(){  
    // 테스트용 사용자 생성  
    testUser1 = User.builder()  
            .id("testuser1")  
            .password("password")  
            .name("Test User1")  
            .nickname("tester1")  
            .phone("01012345678")  
            .manner(new BigDecimal("36.5"))  
            .role("USER")  
            .build();  
  
    testUser2 = User.builder()  
            .id("testuser2")  
            .password("password")  
            .name("Test User2")  
            .nickname("tester2")  
            .phone("01012345678")  
            .manner(new BigDecimal("36.5"))  
            .role("USER")  
            .build();  
  
    usersRepository.save(testUser1);  
    usersRepository.save(testUser2);  
}
```

2. JPA N+1 Test 실행 메서드 : 각 Post에 서로 다른 User를 삽입 후 조회
```Java
    @Test  
    @DisplayName("[JPA N+1 Test] Post 엔티티, User 엔티티 저장 및 조회 테스트")  
    void saveAndFindTest() {  
        // given  
        Post post1 = createPost("첫 번째 게시글", "첫 번째 내용입니다.", "공지사항", testUser1);  
        Post post2 = createPost("두 번째 게시글", "두 번째 내용입니다.", "질문", testUser1);  
        Post post3 = createPost("세 번째 게시글", "세 번째 내용입니다.", "자유", testUser2);  
  
        postRepository.save(post1);  
        postRepository.save(post2);  
        postRepository.save(post3);  
  
        em.flush();  
        em.clear();  
  
        // when  
        List<Post> posts = postRepository.findAll(); //N+1 Test  
  
        // then        for (Post post : posts) {  
            System.out.println("post = " + post.getTitle());  
            System.out.println("post.getUser().getClass() = " + post.getUser().getClass());  
            System.out.println("post.getTeam().getName() = " + post.getUser().getName());  
        }  
    }
```

- 결과
- 첫 번째 게시글 불러오기 (정상)
  ![[n+1 test2.png]]
- 첫 번째 게시글과 연관된 user 불러오기 (문제)
  ![[n+1 test2 1.png]]
- 이후 세 번째 게시글 불러올 때 또다시 user 불러옴(문제)
  ![[n+1 test4.png]]

### 해결 방법1
> 1. fech join을 사용하여 해결하자!

- jpql 코드
```Java
@Query("select p from Post p left join fetch p.user")  
List<Post> findPostbyFetchJoin();
```

- 테스트 코드(수정)
```Java
    @Test  
    @DisplayName("[JPA N+1 Test] Post 엔티티, User 엔티티 저장 및 조회 테스트")  
    void saveAndFindTest() {  
        // given  
        Post post1 = createPost("첫 번째 게시글", "첫 번째 내용입니다.", "공지사항", testUser1);  
        Post post2 = createPost("두 번째 게시글", "두 번째 내용입니다.", "질문", testUser1);  
        Post post3 = createPost("세 번째 게시글", "세 번째 내용입니다.", "자유", testUser2);  
  
        postRepository.save(post1);  
        postRepository.save(post2);  
        postRepository.save(post3);  
  
        em.flush();  
        em.clear();  
  
        // when  
//        List<Post> posts = postRepository.findAll(); //N+1 Test  
        List<Post> posts = postRepository.findPostbyFetchJoin(); //N+1 solve1  
 
  
        // then        for (Post post : posts) {  
            System.out.println("post = " + post.getTitle());  
            System.out.println("post.getUser().getClass() = " + post.getUser().getClass());  
            System.out.println("post.getTeam().getName() = " + post.getUser().getName());  
        }  
    }
```

- 결과
```
2025-05-16T19:39:24.677+09:00 DEBUG 15308 --- [    Test worker] org.hibernate.SQL                        : 
    select
        p1_0.puid,
        p1_0.bookmarks,
        p1_0.content,
        p1_0.created,
        p1_0.hot,
        p1_0.post_location,
        p1_0.post_tag,
        p1_0.post_vote,
        p1_0.subject,
        p1_0.title,
        p1_0.user_id,
        u1_0.uuid,
        u1_0.created,
        u1_0.id,
        u1_0.lastest,
        u1_0.manner,
        u1_0.name,
        u1_0.nickname,
        u1_0.password,
        u1_0.phone,
        u1_0.profile_img,
        u1_0.role,
        p1_0.views 
    from
        posts p1_0 
    left join
        users u1_0 
            on u1_0.uuid=p1_0.user_id
Hibernate: 
    select
        p1_0.puid,
        p1_0.bookmarks,
        p1_0.content,
        p1_0.created,
        p1_0.hot,
        p1_0.post_location,
        p1_0.post_tag,
        p1_0.post_vote,
        p1_0.subject,
        p1_0.title,
        p1_0.user_id,
        u1_0.uuid,
        u1_0.created,
        u1_0.id,
        u1_0.lastest,
        u1_0.manner,
        u1_0.name,
        u1_0.nickname,
        u1_0.password,
        u1_0.phone,
        u1_0.profile_img,
        u1_0.role,
        p1_0.views 
    from
        posts p1_0 
    left join
        users u1_0 
            on u1_0.uuid=p1_0.user_id
post = 첫 번째 게시글
post.getUser().getClass() = class com.example.daangn.domain.user.entity.User
post.getTeam().getName() = Test User1
post = 두 번째 게시글
post.getUser().getClass() = class com.example.daangn.domain.user.entity.User
post.getTeam().getName() = Test User1
post = 세 번째 게시글
post.getUser().getClass() = class com.example.daangn.domain.user.entity.User
post.getTeam().getName() = Test User2
```
- 두, 세 번째 게시글을 가져올 땐 user를 조회하는 일이 없다!






### 해결 방법2
> 1. @EntityGraph을 사용하여 해결하자!

- jpql 코드
```Java
@Override  
@EntityGraph(attributePaths = {"user"})  
List<Post> findAll();
```

- 테스트 코드(수정)
```Java
    @Test  
    @DisplayName("[JPA N+1 Test] Post 엔티티, User 엔티티 저장 및 조회 테스트")  
    void saveAndFindTest() {  
        // given  
        Post post1 = createPost("첫 번째 게시글", "첫 번째 내용입니다.", "공지사항", testUser1);  
        Post post2 = createPost("두 번째 게시글", "두 번째 내용입니다.", "질문", testUser1);  
        Post post3 = createPost("세 번째 게시글", "세 번째 내용입니다.", "자유", testUser2);  
  
        postRepository.save(post1);  
        postRepository.save(post2);  
        postRepository.save(post3);  
  
        em.flush();  
        em.clear();  
  
        // when  
//        List<Post> posts = postRepository.findAll(); //N+1 Test  
//        List<Post> posts = postRepository.findPostbyFetchJoin(); //N+1 solve1  
		List<Post> posts = postRepository.findAll(); //N+1 solve2
  
        // then        for (Post post : posts) {  
            System.out.println("post = " + post.getTitle());  
            System.out.println("post.getUser().getClass() = " + post.getUser().getClass());  
            System.out.println("post.getTeam().getName() = " + post.getUser().getName());  
        }  
    }
```

- 결과
```
2025-05-16T19:45:15.795+09:00 DEBUG 16568 --- [    Test worker] org.hibernate.SQL                        : 
    select
        p1_0.puid,
        p1_0.bookmarks,
        p1_0.content,
        p1_0.created,
        p1_0.hot,
        p1_0.post_location,
        p1_0.post_tag,
        p1_0.post_vote,
        p1_0.subject,
        p1_0.title,
        p1_0.user_id,
        u1_0.uuid,
        u1_0.created,
        u1_0.id,
        u1_0.lastest,
        u1_0.manner,
        u1_0.name,
        u1_0.nickname,
        u1_0.password,
        u1_0.phone,
        u1_0.profile_img,
        u1_0.role,
        p1_0.views 
    from
        posts p1_0 
    join
        users u1_0 
            on u1_0.uuid=p1_0.user_id
Hibernate: 
    select
        p1_0.puid,
        p1_0.bookmarks,
        p1_0.content,
        p1_0.created,
        p1_0.hot,
        p1_0.post_location,
        p1_0.post_tag,
        p1_0.post_vote,
        p1_0.subject,
        p1_0.title,
        p1_0.user_id,
        u1_0.uuid,
        u1_0.created,
        u1_0.id,
        u1_0.lastest,
        u1_0.manner,
        u1_0.name,
        u1_0.nickname,
        u1_0.password,
        u1_0.phone,
        u1_0.profile_img,
        u1_0.role,
        p1_0.views 
    from
        posts p1_0 
    join
        users u1_0 
            on u1_0.uuid=p1_0.user_id
post = 첫 번째 게시글
post.getUser().getClass() = class com.example.daangn.domain.user.entity.User
post.getTeam().getName() = Test User1
post = 두 번째 게시글
post.getUser().getClass() = class com.example.daangn.domain.user.entity.User
post.getTeam().getName() = Test User1
post = 세 번째 게시글
post.getUser().getClass() = class com.example.daangn.domain.user.entity.User
post.getTeam().getName() = Test User2
```
- fetch join과 마찬가지로 두, 세 번째 게시글을 가져올 땐 user를 조회하는 일이 없다!







### 결론
>[!important] JPA를 쓴다면 항상 N+1을 유의하자!
>- JPA는 편리해서 좋지만 N+1이라는 위험성을 항상 지니고 있다는 걸 명심하자
>- 현재 테스트 상황은 일부로 테이블이 작고 테이블간의 연관성도 적게 해놓았지만 프로젝트의 규가 커질수록 이에 대한 중요서을 뼈저리게 느낄 것 같다.


## 3️⃣ Service 계층의 단위 테스트를 진행해요
---------
### Service 계층 테스트
>[!info] Business 로직에 대한 검증
>- Repository 테스트 : CRUD 위주의 DB 쿼리문에 대한 검증
   >  - Service 테스트 : CRUD를 행하기 전 데이터 전처리 및 비즈니스 로직에 대한 검증

### 검증 예시
>[!info] UserService에 대하여 테스트를 진행하였습니다.

- 간단 회원 가입 검증
```Java
/** 새로운 유저 추가 (회원가입)*/  
public User join(User user) {  
    //Security 적용 전이라 간단한 회원 비교 로직 작성  
    if(userRepository.existsById(user.getId())) {  
        return null;  
    }  
    return userRepository.save(user);  
}
```

- 테스트 코드
1. 사전 작업
```Java
@BeforeEach  
public void setup(){  
    // 테스트용 사용자 생성  
    User u1 = User.builder()  
            .id("testuser1")  
            .password("password")  
            .name("Test User1")  
            .nickname("tester1")  
            .phone("01012345678")  
            .manner(new BigDecimal("36.5"))  
            .role("USER")  
            .build();  
  
    testUser1 = userService.join(u1);  
}
```

2. 동일한 ID를 가진 user를 회원가입 시킴(검증)
```Java
@Test  
public void test(){  
    //given  
    //같은 Id를 가진 user를 삽입  
    User u2 = User.builder()  
            .id("testuser1")  
            .password("password")  
            .name("Test User2")  
            .nickname("tester2")  
            .phone("01012345678")  
            .manner(new BigDecimal("36.5"))  
            .role("USER")  
            .build();  
  
  
    //when  
    User newUser = userService.join(u2);  
  
    //then  
    if(newUser == null){  
        System.out.println("회원 가입 실패: 이미 존재하는 유저입니다.");  
        return;  
    }  
  
    System.out.println("회원 가입 성공!");  
}
```

- 결과
![[service test.png]]
-> 동일한 ID로 회원가입을 시도했기에 existsById에서 true값을 받아 null을 인식함

### 결론
> [!NOTE]
> - 로직 분리는 확실하게
>- 내가 작성한 비즈니스 로직과 데이터를 실제로 CRUD 하는 로직은 따로 분리하여 관리를 하자
>- 프로젝트 진행 시 유지보수와 향후 코드 검증을 위해서는 Service 단과 Repository 단을 구분하여 작업하는 것이 좋을 것 같다.


