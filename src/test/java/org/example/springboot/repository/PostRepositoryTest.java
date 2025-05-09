package org.example.springboot.repository;

import org.example.springboot.domain.Post;
import org.example.springboot.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    public void setup() {
        // 테스트 사용자 생성 - Builder 사용
        user1 = User.builder()
                .email("user1@example.com")
                .password("password1")
                .phone("010-1111-1111")
                .name("사용자1")
                .profile("안녕하세요 사용자1입니다.")
                .nickname("닉네임1")
                .profileImgPath("/images/profile1.jpg")
                .role("USER")
                .build();
        userRepository.save(user1);

        user2 = User.builder()
                .email("user2@example.com")
                .password("password2")
                .phone("010-2222-2222")
                .name("사용자2")
                .profile("안녕하세요 사용자2입니다.")
                .nickname("닉네임2")
                .profileImgPath("/images/profile2.jpg")
                .role("USER")
                .build();
        userRepository.save(user2);
    }

    @Test
    @DisplayName("게시물 등록 및 조회 테스트")
    public void createAndFindPosts() {
        // 테스트 게시물 생성 - Builder 사용
        Post post1 = Post.builder()
                .user(user1)
                .title("첫 번째 게시물")
                .content("첫 번째 게시물 내용입니다.")
                .status("판매중")
                .build();

        Post post2 = Post.builder()
                .user(user1)
                .title("두 번째 게시물")
                .content("두 번째 게시물 내용입니다.")
                .status("판매중")
                .build();

        Post post3 = Post.builder()
                .user(user2)
                .title("세 번째 게시물")
                .content("세 번째 게시물 내용입니다.")
                .status("판매완료")
                .build();

        // 게시물 저장
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // 전체 게시물 조회 테스트
        List<Post> allPosts = postRepository.findAll();
        assertThat(allPosts).hasSize(3);

        // ID로 게시물 조회 테스트
        Post foundPost = postRepository.findById(post1.getPostId()).orElse(null);
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getTitle()).isEqualTo("첫 번째 게시물");

        // 사용자별 게시물 조회 테스트
        List<Post> user1Posts = postRepository.findByUser(user1);
        assertThat(user1Posts).hasSize(2);
        assertThat(user1Posts.get(0).getUser().getEmail()).isEqualTo("user1@example.com");

        // 상태별 게시물 조회 테스트
        List<Post> sellingPosts = postRepository.findByStatus("판매중");
        assertThat(sellingPosts).hasSize(2);

        List<Post> soldPosts = postRepository.findByStatus("판매완료");
        assertThat(soldPosts).hasSize(1);
        assertThat(soldPosts.get(0).getUser().getEmail()).isEqualTo("user2@example.com");

        // 최신순 게시물 조회 테스트
        List<Post> latestPosts = postRepository.findAllOrderByCreatedAtDesc();
        assertThat(latestPosts).hasSize(3);
        // 최신 게시물이 먼저 조회되므로 가장 나중에 저장한 게시물이 첫 번째로 나옴
        assertThat(latestPosts.get(0).getTitle()).isEqualTo("세 번째 게시물");
    }
}