package com.example.daangn.service;

import com.example.daangn.domain.Post;
import com.example.daangn.domain.PostStatus;
import com.example.daangn.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public List<Post> findAllByUserUserId(Long userId) {
        return postRepository.findAllByUserUserId(userId);
    }

    @Override
    public List<Post> getPostsByStatus(PostStatus status) {
        return postRepository.findAllByStatus(status);
    }

    @Override
    public List<Post> searchPostsByTitle(String keyword) {
        return postRepository.findByTitleContaining(keyword);
    }
}
