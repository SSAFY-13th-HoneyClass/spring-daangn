package org.example.springboot.repository;

import org.example.springboot.domain.Post;
import org.example.springboot.domain.PostLike;
import org.example.springboot.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class PostLikeRepositoryTest {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;
    private Post post1;
    private Post post2;

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

        user3 = User.builder()
                .email("user3@example.com")
                .password("password3")
                .phone("010-3333-3333")
                .name("사용자3")
                .profile("안녕하세요 사용자3입니다.")
                .nickname("닉네임3")
                .profileImgPath("/images/profile3.jpg")
                .role("USER")
                .build();
        userRepository.save(user3);

        // 테스트 게시물 생성 - Builder 사용
        post1 = Post.builder()
                .user(user1)
                .title("첫 번째 게시물")
                .content("첫 번째 게시물 내용입니다.")
                .status("판매중")
                .build();
        postRepository.save(post1);

        post2 = Post.builder()
                .user(user2)
                .title("두 번째 게시물")
                .content("두 번째 게시물 내용입니다.")
                .status("판매중")
                .build();
        postRepository.save(post2);
    }

    @Test
    @DisplayName("게시물 좋아요 등록 및 조회 테스트")
    public void createAndFindPostLikes() {
        // user1이 post2에 좋아요 - Builder 사용
        PostLike postLike1 = PostLike.builder()
                .post(post2)
                .user(user1)
                .build();
        postLikeRepository.save(postLike1);

        // user2가 post1에 좋아요 - Builder 사용
        PostLike postLike2 = PostLike.builder()
                .post(post1)
                .user(user2)
                .build();
        postLikeRepository.save(postLike2);

        // user3가 post1과 post2 모두 좋아요 - Builder 사용
        PostLike postLike3 = PostLike.builder()
                .post(post1)
                .user(user3)
                .build();
        postLikeRepository.save(postLike3);

        PostLike postLike4 = PostLike.builder()
                .post(post2)
                .user(user3)
                .build();
        postLikeRepository.save(postLike4);

        // 전체 좋아요 조회 테스트
        List<PostLike> allPostLikes = postLikeRepository.findAll();
        assertThat(allPostLikes).hasSize(4);

        // 게시물별 좋아요 조회 테스트
        List<PostLike> post1Likes = postLikeRepository.findByPost(post1);
        assertThat(post1Likes).hasSize(2);

        List<PostLike> post2Likes = postLikeRepository.findByPost(post2);
        assertThat(post2Likes).hasSize(2);

        // 사용자별 좋아요 조회 테스트
        List<PostLike> user1Likes = postLikeRepository.findByUser(user1);
        assertThat(user1Likes).hasSize(1);
        assertThat(user1Likes.get(0).getPost().getTitle()).isEqualTo("두 번째 게시물");

        List<PostLike> user3Likes = postLikeRepository.findByUser(user3);
        assertThat(user3Likes).hasSize(2);

        // 특정 게시물+사용자 좋아요 조회 테스트
        Optional<PostLike> user2LikesPost1 = postLikeRepository.findByPostAndUser(post1, user2);
        assertThat(user2LikesPost1).isPresent();

        // 좋아요 존재 여부 테스트
        boolean user1LikesPost2 = postLikeRepository.existsByPostAndUser(post2, user1);
        assertThat(user1LikesPost2).isTrue();

        boolean user2LikesPost2 = postLikeRepository.existsByPostAndUser(post2, user2);
        assertThat(user2LikesPost2).isFalse();

        // 좋아요 개수 테스트
        Long post1LikesCount = postLikeRepository.countByPostId(post1.getPostId());
        assertThat(post1LikesCount).isEqualTo(2);

        Long post2LikesCount = postLikeRepository.countByPostId(post2.getPostId());
        assertThat(post2LikesCount).isEqualTo(2);
    }
}