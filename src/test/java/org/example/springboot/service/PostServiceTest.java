package org.example.springboot.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.springboot.domain.Post;
import org.example.springboot.domain.User;
import org.example.springboot.dto.PostDto;
import org.example.springboot.repository.PostRepository;
import org.example.springboot.repository.UserRepository;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.jpa.properties.hibernate.generate_statistics=true",
        "spring.jpa.properties.hibernate.default_batch_fetch_size=0" // N+1 문제를 명확히 보기 위해 배치 사이즈 비활성화
})
@Transactional
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;
    private Statistics statistics;

    @BeforeEach
    public void setup() {
        // Hibernate 통계를 활성화하여 쿼리 개수 추적
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        statistics = sessionFactory.getStatistics();
        statistics.clear();

        // 테스트용 사용자 생성
        testUser = User.builder()
                .email("post-test@example.com")
                .password("password")
                .phone("010-1234-5678")
                .name("게시물테스트")
                .profile("안녕하세요")
                .nickname("게시물닉네임")
                .profileImgPath("/images/post-test.jpg")
                .role("USER")
                .build();
        userRepository.save(testUser);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("게시물 생성 - 성공")
    public void createPost_Success() {
        // given
        PostDto.CreateRequest requestDto = PostDto.CreateRequest.builder()
                .title("테스트 게시물")
                .content("테스트 게시물 내용입니다.")
                .build();

        // when
        Long postId = postService.createPost(requestDto, testUser.getUserId());

        // then
        Post savedPost = postRepository.findById(postId).orElse(null);
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getTitle()).isEqualTo("테스트 게시물");
        assertThat(savedPost.getContent()).isEqualTo("테스트 게시물 내용입니다.");
        assertThat(savedPost.getStatus()).isEqualTo("판매중");
        assertThat(savedPost.getUser().getUserId()).isEqualTo(testUser.getUserId());
    }

    @Test
    @DisplayName("게시물 상세 조회 - 성공 및 N+1 문제 확인")
    public void getPostDetail_Success() {
        // given
        Post post = Post.builder()
                .user(testUser)
                .title("조회 테스트")
                .content("조회 테스트 내용입니다.")
                .status("판매중")
                .build();
        postRepository.save(post);

        entityManager.flush();
        entityManager.clear();

        statistics.clear(); // 통계 초기화

        // when
        // 직접 리포지토리 호출을 통해 쿼리 개수 확인
        Post foundPost = postRepository.findById(post.getPostId()).orElseThrow();
        String userName = foundPost.getUser().getName(); // N+1 문제 발생 지점

        // then
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getTitle()).isEqualTo("조회 테스트");

        // 실행된 쿼리 수 확인 (게시물 조회 1번 + 사용자 조회 1번 = 2번)
        long queryCount = statistics.getQueryExecutionCount();
        System.out.println("상세 조회 시 실행된 쿼리 개수: " + queryCount);
        assertThat(queryCount).isEqualTo(2);
    }

    @Test
    @DisplayName("게시물 상태 업데이트 - 성공")
    public void updatePostStatus_Success() {
        // given
        Post post = Post.builder()
                .user(testUser)
                .title("상태 업데이트 테스트")
                .content("상태 업데이트 테스트 내용입니다.")
                .status("판매중")
                .build();
        postRepository.save(post);

        entityManager.flush();
        entityManager.clear();

        // when
        postService.updatePostStatus(post.getPostId(), "예약중", testUser.getUserId());

        // then
        Post updatedPost = postRepository.findById(post.getPostId()).orElse(null);
        assertThat(updatedPost).isNotNull();
        assertThat(updatedPost.getStatus()).isEqualTo("예약중");
    }

    @Test
    @DisplayName("게시물 상태 업데이트 - 게시물 작성자가 아닌 경우 실패")
    public void updatePostStatus_NotPostOwner_Fail() {
        // given
        Post post = Post.builder()
                .user(testUser)
                .title("권한 테스트")
                .content("권한 테스트 내용입니다.")
                .status("판매중")
                .build();
        postRepository.save(post);

        // 다른 사용자 생성
        User otherUser = User.builder()
                .email("other@example.com")
                .password("password")
                .phone("010-9999-9999")
                .name("다른사용자")
                .profile("안녕하세요")
                .nickname("다른닉네임")
                .profileImgPath("/images/other.jpg")
                .role("USER")
                .build();
        userRepository.save(otherUser);

        entityManager.flush();
        entityManager.clear();

        // when & then
        assertThatThrownBy(() -> postService.updatePostStatus(post.getPostId(), "예약중", otherUser.getUserId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("게시물 수정 권한이 없습니다.");
    }

    @Test
    @DisplayName("N+1 문제 발생과 해결 테스트")
    public void testNPlusOneProblem() {
        // given
        // 여러 사용자 생성
        for (int i = 0; i < 5; i++) {
            User user = User.builder()
                    .email("user" + i + "@example.com")
                    .password("password")
                    .phone("010-1111-111" + i)
                    .name("사용자" + i)
                    .profile("안녕하세요")
                    .nickname("닉네임" + i)
                    .profileImgPath("/images/user" + i + ".jpg")
                    .role("USER")
                    .build();
            userRepository.save(user);

            // 각 사용자별로 게시물 생성
            Post post = Post.builder()
                    .user(user)
                    .title("N+1 테스트 게시물 " + i)
                    .content("N+1 테스트 내용 " + i)
                    .status("판매중")
                    .build();
            postRepository.save(post);
        }

        entityManager.flush();
        entityManager.clear();
        statistics.clear();

        // 테스트 1: N+1 문제 확인을 위한 직접 쿼리 실행
        // 게시물 조회 후 각 게시물의 User 접근 시 추가 쿼리 발생하는지 확인
        System.out.println("===== N+1 문제 발생 확인 =====");
        List<Post> posts = entityManager.createQuery("SELECT p FROM Post p WHERE p.status = :status", Post.class)
                .setParameter("status", "판매중")
                .getResultList();

        System.out.println("게시물 조회 후 각 사용자 정보 접근:");
        for (Post post : posts) {
            // 지연 로딩된 사용자 정보에 접근하면 각각 추가 쿼리 발생
            System.out.println("게시물: " + post.getTitle() + ", 작성자: " + post.getUser().getNickname());
        }

        long queryCount = statistics.getQueryExecutionCount();
        System.out.println("총 실행된 쿼리 개수: " + queryCount);

        // 게시물 조회 1번 + 게시물별 사용자 조회 5번 = 최소 6번 이상 쿼리 실행
        assertThat(queryCount).isGreaterThanOrEqualTo(6);

        // 테스트 2: Fetch Join으로 N+1 문제 해결
        entityManager.clear();
        statistics.clear();

        System.out.println("===== Fetch Join으로 N+1 문제 해결 확인 =====");
        List<Post> joinedPosts = entityManager.createQuery(
                        "SELECT p FROM Post p JOIN FETCH p.user WHERE p.status = :status", Post.class)
                .setParameter("status", "판매중")
                .getResultList();

        System.out.println("Fetch Join으로 조회 후 각 사용자 정보 접근:");
        for (Post post : joinedPosts) {
            // 이미 로딩된 사용자 정보에 접근해도 추가 쿼리 발생 안함
            System.out.println("게시물: " + post.getTitle() + ", 작성자: " + post.getUser().getNickname());
        }

        long joinQueryCount = statistics.getQueryExecutionCount();
        System.out.println("Fetch Join 사용 시 총 실행된 쿼리 개수: " + joinQueryCount);

        // Fetch Join 사용 시 단 1번의 쿼리로 모든 데이터 조회 가능
        assertThat(joinQueryCount).isEqualTo(1);
    }
}