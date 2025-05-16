package com.example.daangn.repository;

import com.example.daangn.domain.post.entity.Post;
import com.example.daangn.domain.post.repository.PostRepository;
import com.example.daangn.domain.user.entity.User;
import com.example.daangn.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
public class PostRepositoryJPATest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository usersRepository;

    @PersistenceContext
    private EntityManager em;

    private User testUser1, testUser2;


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

        // then
        for (Post post : posts) {
            System.out.println("post = " + post.getTitle());
            System.out.println("post.getUser().getClass() = " + post.getUser().getClass());
            System.out.println("post.getTeam().getName() = " + post.getUser().getName());
        }
    }

    // 테스트 도우미 메소드
    private Post createPost(String title, String content, String subject, User testUser) {
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
