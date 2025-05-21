package com.example.daangn.repository;

import com.example.daangn.domain.Post;
import com.example.daangn.domain.PostStatus;
import com.example.daangn.domain.Role;
import com.example.daangn.domain.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("User와 연관된 Post 조회 시 1+N 문제 확인")
    void testOnePlusOne() {
        // given
        User user = userRepository.save(User.builder()
                .name("홍정인")
                .email("ss@s.s")
                .password("1234")
                .role(Role.USER)
                .build());

        for (int i = 0; i < 10; i++) {
            postRepository.save(Post.builder()
                    .user(user)
                    .title("제목" + i)
                    .content("내용" + i)
                    .status(PostStatus.RESERVED)
                    .build());
        }
        em.flush();
        em.clear();

        // when
        List<Post> posts = postRepository.findAll();

        // then
        for (Post post : posts) {
            System.out.println(post.getUser().getName());
        }

        assertThat(posts).hasSize(10);
    }
}
