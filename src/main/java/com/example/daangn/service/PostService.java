package com.example.daangn.service;

import com.example.daangn.domain.Post;
import com.example.daangn.domain.PostStatus;

import java.util.List;

public interface PostService {
    List<Post> findAllByUserUserId(Long userId);
    List<Post> getPostsByStatus(PostStatus status);
    List<Post> searchPostsByTitle(String keyword);
}
