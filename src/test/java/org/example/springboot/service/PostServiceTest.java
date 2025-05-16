package org.example.springboot.service;

import org.example.springboot.domain.Post;
import org.example.springboot.domain.User;
import org.example.springboot.dto.PostDto;
import org.example.springboot.repository.PostRepository;
import org.example.springboot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private User testUser;

    @BeforeEach
    public void setup() {
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
    @DisplayName("게시물 상세 조회 - 성공")
    public void getPostDetail_Success() {
        // given
        Post post = Post.builder()
                .user(testUser)
                .title("조회 테스트")
                .content("조회 테스트 내용입니다.")
                .status("판매중")
                .build();
        postRepository.save(post);

        // when
        PostDto.DetailResponse response = postService.getPostDetail(post.getPostId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("조회 테스트");
        assertThat(response.getUserId()).isEqualTo(testUser.getUserId());
        assertThat(response.getNickname()).isEqualTo(testUser.getNickname());
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

        // when & then
        assertThatThrownBy(() -> postService.updatePostStatus(post.getPostId(), "예약중", otherUser.getUserId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("게시물 수정 권한이 없습니다.");
    }
}