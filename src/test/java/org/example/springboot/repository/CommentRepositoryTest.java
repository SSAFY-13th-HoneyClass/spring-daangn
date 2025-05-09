package org.example.springboot.repository;

import org.example.springboot.domain.Comment;
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
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private Post post;

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

        // 테스트 게시물 생성 - Builder 사용
        post = Post.builder()
                .user(user1)
                .title("테스트 게시물")
                .content("테스트 게시물 내용입니다.")
                .status("판매중")
                .build();
        postRepository.save(post);
    }

    @Test
    @DisplayName("댓글 등록 및 조회 테스트")
    public void createAndFindComments() {
        // given
        Comment rootComment1 = Comment.builder()
                .post(post)
                .user(user1)
                .content("첫 번째 댓글입니다.")
                .build();

        Comment rootComment2 = Comment.builder()
                .post(post)
                .user(user2)
                .content("두 번째 댓글입니다.")
                .build();

        // when
        commentRepository.save(rootComment1);
        commentRepository.save(rootComment2);

        Comment childComment1 = Comment.builder()
                .post(post)
                .user(user2)
                .parentComment(rootComment1)
                .content("첫 번째 댓글의 대댓글입니다.")
                .build();
        commentRepository.save(childComment1);

        Comment childComment2 = Comment.builder()
                .post(post)
                .user(user1)
                .parentComment(rootComment1)
                .content("첫 번째 댓글의 두 번째 대댓글입니다.")
                .build();
        commentRepository.save(childComment2);

        List<Comment> postComments = commentRepository.findByPost(post);
        List<Comment> rootComments = commentRepository.findRootCommentsByPostId(post.getPostId());
        List<Comment> childComments = commentRepository.findByParentComment(rootComment1);
        Comment foundComment = commentRepository.findById(rootComment1.getCommentId()).orElse(null);
        Comment foundChildComment = commentRepository.findById(childComment1.getCommentId()).orElse(null);

        // then
        assertThat(postComments).hasSize(4); // 루트 댓글 2개 + 대댓글 2개

        assertThat(rootComments).hasSize(2);

        assertThat(childComments).hasSize(2);

        assertThat(foundComment).isNotNull();
        assertThat(foundComment.getContent()).isEqualTo("첫 번째 댓글입니다.");

        assertThat(foundChildComment).isNotNull();
        assertThat(foundChildComment.getParentComment().getCommentId()).isEqualTo(rootComment1.getCommentId());
    }
}