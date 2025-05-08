package com.ssafy.daangn.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ssafy.daangn.domain.Comment;
import com.ssafy.daangn.domain.Post;
import com.ssafy.daangn.domain.User;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("given 3 comments_when findAll_then return all 3 and log SQL")
    void givenComments_whenFindAll_thenReturnAll() {
        // given
        User user = new User("tester", "tester@example.com", "pwd", null);
        user = userRepository.save(user);

        Post post = new Post(user, "Test Post", "Content");
        post = postRepository.save(post);

        Comment c1 = new Comment(post, user, null, "First comment");
        Comment c2 = new Comment(post, user, null, "Second comment");
        Comment c3 = new Comment(post, user, null, "Third comment");

        commentRepository.saveAll(List.of(c1, c2, c3));

        // when
        List<Comment> result = commentRepository.findAll();

        // then
        assertThat(result).hasSize(3)
            .extracting(Comment::getContent)
            .containsExactlyInAnyOrder(
                "First comment", 
                "Second comment", 
                "Third comment"
            );
    }
}