package com.example.daangn.repository;

import com.example.daangn.domain.post.entity.Post;
import com.example.daangn.domain.post.repository.PostRepository;
import com.example.daangn.domain.user.entity.User;
import com.example.daangn.domain.user.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UsersRepository usersRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = User.builder()
                .id("testuser")
                .password("password")
                .name("Test User")
                .nickname("tester")
                .phone("01012345678")
                .manner(new BigDecimal("36.5"))
                .role("USER")
                .build();

        usersRepository.save(testUser);
    }

    @Test
    @DisplayName("Post 엔티티 저장 및 조회 테스트")
    void saveAndFindTest() {
        // given
        Post post1 = createPost("첫 번째 게시글", "첫 번째 내용입니다.", "공지사항");
        Post post2 = createPost("두 번째 게시글", "두 번째 내용입니다.", "질문");
        Post post3 = createPost("세 번째 게시글", "세 번째 내용입니다.", "자유");

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // when
        List<Post> foundPosts = postRepository.findAll();

        // then
        assertThat(foundPosts).isNotNull();
        assertThat(foundPosts.size()).isEqualTo(3);
        assertThat(foundPosts).extracting("title")
                .containsExactlyInAnyOrder("첫 번째 게시글", "두 번째 게시글", "세 번째 게시글");
    }

    @Test
    @DisplayName("Post 엔티티 ID로 조회 테스트")
    void findByIdTest() {
        // given
        Post post = createPost("테스트 게시글", "테스트 내용입니다.", "공지사항");
        postRepository.save(post);

        // when
        Optional<Post> foundPost = postRepository.findById(post.getPuid());

        // then
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo("테스트 게시글");
        assertThat(foundPost.get().getContent()).isEqualTo("테스트 내용입니다.");
    }

    @Test
    @DisplayName("Post 사용자별 조회 테스트")
    void findByUserTest() {
        // given
        // 첫 번째 사용자의 게시글
        Post post1 = createPost("첫 번째 게시글", "첫 번째 내용입니다.", "공지사항");
        Post post2 = createPost("두 번째 게시글", "두 번째 내용입니다.", "질문");
        Post post3 = createPost("세 번째 게시글", "세 번째 내용입니다.", "자유");

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // 두 번째 사용자 생성 및 게시글 작성
        User anotherUser = User.builder()
                .id("anotheruser")
                .password("password")
                .name("Another User")
                .nickname("another")
                .phone("01098765432")
                .manner(new BigDecimal("36.5"))
                .role("USER")
                .build();

        usersRepository.save(anotherUser);

        Post anotherPost = Post.builder()
                .user(anotherUser)
                .subject("다른 주제")
                .title("다른 사용자의 게시글")
                .content("다른 사용자가 작성한 내용입니다.")
                .created(LocalDateTime.now())
                .views(0)
                .bookmarks(0)
                .build();

        postRepository.save(anotherPost);

        // when
        Page<Post> foundPosts = postRepository.findByUser(testUser,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created")));

        // then
        assertThat(foundPosts).isNotNull();
        assertThat(foundPosts.getTotalElements()).isEqualTo(3);
        assertThat(foundPosts.getContent()).extracting("title")
                .containsExactlyInAnyOrder("첫 번째 게시글", "두 번째 게시글", "세 번째 게시글");
    }

    @Test
    @DisplayName("Post 업데이트 테스트")
    void updatePostTest() {
        // given
        Post post = createPost("원래 제목", "원래 내용", "공지사항");
        postRepository.save(post);

        // when
        post.setTitle("수정된 제목");
        post.setContent("수정된 내용");
        postRepository.save(post);

        // then
        Optional<Post> updatedPost = postRepository.findById(post.getPuid());
        assertThat(updatedPost).isPresent();
        assertThat(updatedPost.get().getTitle()).isEqualTo("수정된 제목");
        assertThat(updatedPost.get().getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("Post 삭제 테스트")
    void deletePostTest() {
        // given
        Post post1 = createPost("첫 번째 게시글", "첫 번째 내용입니다.", "공지사항");
        Post post2 = createPost("두 번째 게시글", "두 번째 내용입니다.", "질문");
        Post post3 = createPost("삭제할 게시글", "삭제할 내용입니다.", "자유");

        postRepository.saveAll(List.of(post1, post2, post3));

        // when
        postRepository.delete(post3);

        // then
        List<Post> remainingPosts = postRepository.findAll();
        assertThat(remainingPosts.size()).isEqualTo(2);
        assertThat(remainingPosts).extracting("title")
                .containsExactlyInAnyOrder("첫 번째 게시글", "두 번째 게시글");

        Optional<Post> deletedPost = postRepository.findById(post3.getPuid());
        assertThat(deletedPost).isNotPresent();
    }

    @Test
    @DisplayName("키워드로 게시글 검색 테스트")
    void searchPostsByKeywordTest() {
        // given
        Post post1 = createPost("자바 프로그래밍", "자바 프로그래밍에 대한 내용입니다.", "개발");
        Post post2 = createPost("파이썬 프로그래밍", "파이썬 프로그래밍에 대한 내용입니다.", "개발");
        Post post3 = createPost("스프링 부트", "자바 기반의 스프링 부트 프레임워크입니다.", "개발");

        postRepository.saveAll(List.of(post1, post2, post3));

        // when
        Page<Post> javaResults = postRepository.findByTitleContainingOrContentContaining(
                "자바", "자바", PageRequest.of(0, 10));

        // then
        assertThat(javaResults.getTotalElements()).isEqualTo(2);
        assertThat(javaResults.getContent()).extracting("title")
                .containsExactlyInAnyOrder("자바 프로그래밍", "스프링 부트");
    }

    @Test
    @DisplayName("인기 게시글 조회 테스트")
    void findTopPostsByViewsTest() {
        // given
        Post post1 = createPost("인기 게시글 1", "내용 1", "자유");
        post1.setViews(100);

        Post post2 = createPost("인기 게시글 2", "내용 2", "자유");
        post2.setViews(50);

        Post post3 = createPost("인기 게시글 3", "내용 3", "자유");
        post3.setViews(200);

        Post post4 = createPost("인기 게시글 4", "내용 4", "자유");
        post4.setViews(30);

        Post post5 = createPost("인기 게시글 5", "내용 5", "자유");
        post5.setViews(150);

        postRepository.saveAll(List.of(post1, post2, post3, post4, post5));

        // when
        List<Post> topPosts = postRepository.findTop10ByOrderByViewsDesc();

        // then
        assertThat(topPosts).hasSize(5);
        assertThat(topPosts.get(0).getTitle()).isEqualTo("인기 게시글 3");  // 200 views
        assertThat(topPosts.get(1).getTitle()).isEqualTo("인기 게시글 5");  // 150 views
        assertThat(topPosts.get(2).getTitle()).isEqualTo("인기 게시글 1");  // 100 views
    }

    // 테스트 도우미 메소드
    private Post createPost(String title, String content, String subject) {
        return Post.builder()
                .user(testUser)
                .subject(subject)
                .title(title)
                .content(content)
                .created(LocalDateTime.now())
                .views(0)
                .bookmarks(0)
                .build();
    }
}
