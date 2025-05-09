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
        // 테스트 사용자 생성
        user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1.setPhone("010-1111-1111");
        user1.setName("사용자1");
        user1.setProfile("안녕하세요 사용자1입니다.");
        user1.setNickname("닉네임1");
        user1.setProfileImgPath("/images/profile1.jpg");
        user1.setRole("USER");
        userRepository.save(user1);

        user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        user2.setPhone("010-2222-2222");
        user2.setName("사용자2");
        user2.setProfile("안녕하세요 사용자2입니다.");
        user2.setNickname("닉네임2");
        user2.setProfileImgPath("/images/profile2.jpg");
        user2.setRole("USER");
        userRepository.save(user2);

        // 테스트 게시물 생성
        post = new Post();
        post.setUser(user1);
        post.setTitle("테스트 게시물");
        post.setContent("테스트 게시물 내용입니다.");
        post.setStatus("판매중");
        postRepository.save(post);
    }

    @Test
    @DisplayName("댓글 등록 및 조회 테스트")
    public void createAndFindComments() {
        // 루트 댓글 생성
        Comment rootComment1 = new Comment();
        rootComment1.setPost(post);
        rootComment1.setUser(user1);
        rootComment1.setContent("첫 번째 댓글입니다.");
        commentRepository.save(rootComment1);

        Comment rootComment2 = new Comment();
        rootComment2.setPost(post);
        rootComment2.setUser(user2);
        rootComment2.setContent("두 번째 댓글입니다.");
        commentRepository.save(rootComment2);

        // 대댓글 생성
        Comment childComment1 = new Comment();
        childComment1.setPost(post);
        childComment1.setUser(user2);
        childComment1.setParentComment(rootComment1);
        childComment1.setContent("첫 번째 댓글의 대댓글입니다.");
        commentRepository.save(childComment1);

        Comment childComment2 = new Comment();
        childComment2.setPost(post);
        childComment2.setUser(user1);
        childComment2.setParentComment(rootComment1);
        childComment2.setContent("첫 번째 댓글의 두 번째 대댓글입니다.");
        commentRepository.save(childComment2);

        // 게시물 댓글 조회 테스트
        List<Comment> postComments = commentRepository.findByPost(post);
        assertThat(postComments).hasSize(4); // 루트 댓글 2개 + 대댓글 2개

        // 루트 댓글만 조회 테스트
        List<Comment> rootComments = commentRepository.findRootCommentsByPostId(post.getPostId());
        assertThat(rootComments).hasSize(2);

        // 특정 댓글의 대댓글 조회 테스트
        List<Comment> childComments = commentRepository.findByParentComment(rootComment1);
        assertThat(childComments).hasSize(2);

        // 댓글 ID로 조회 테스트
        Comment foundComment = commentRepository.findById(rootComment1.getCommentId()).orElse(null);
        assertThat(foundComment).isNotNull();
        assertThat(foundComment.getContent()).isEqualTo("첫 번째 댓글입니다.");

        // 대댓글 내용 확인
        Comment foundChildComment = commentRepository.findById(childComment1.getCommentId()).orElse(null);
        assertThat(foundChildComment).isNotNull();
        assertThat(foundChildComment.getParentComment().getCommentId()).isEqualTo(rootComment1.getCommentId());
    }
}