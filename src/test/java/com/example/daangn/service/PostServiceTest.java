package com.example.daangn.service;

import com.example.daangn.domain.Post;
import com.example.daangn.domain.PostStatus;
import com.example.daangn.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post1;
    private Post post2;

    @BeforeEach
    void setup() {
        post1 = Post.builder().postId(1L).title("p1").status(PostStatus.ACTIVE).build();
        post2 = Post.builder().postId(2L).title("p2").status(PostStatus.ACTIVE).build();
    }

    @Test
    void testGetPostsByStatus() {
        // given
        when(postRepository.findAllByStatus(PostStatus.ACTIVE)).thenReturn(Arrays.asList(post1, post2));

        // when
        List<Post> result = postService.getPostsByStatus(PostStatus.ACTIVE);

        // then
        assertThat(result).hasSize(2);
        verify(postRepository, times(1)).findAllByStatus(PostStatus.ACTIVE);
    }

    @Test
    void testSearchPostsByTitle() {
        // given
        when(postRepository.findByTitleContaining("p1")).thenReturn(List.of(post1));

        // when
        List<Post> result = postService.searchPostsByTitle("p1");

        // then
        assertThat(result.get(0).getTitle()).isEqualTo("p1");
    }
}
