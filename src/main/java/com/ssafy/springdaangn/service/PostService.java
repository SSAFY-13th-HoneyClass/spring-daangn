package com.ssafy.springdaangn.service;

import com.ssafy.springdaangn.Domain.Post;
import com.ssafy.springdaangn.Domain.User;
import com.ssafy.springdaangn.Exception.PostNotFoundException;
import com.ssafy.springdaangn.Exception.UserNotFoundException;
import com.ssafy.springdaangn.Repository.PostRepository;
import com.ssafy.springdaangn.Repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Post createPost(Long userId, Post post) {
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        post.setSeller(seller);
        return postRepository.save(post);
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    public List<Post> getPostsByUser(Long userId) {
        // 사용자 존재 여부 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return postRepository.findBySellerUserId(userId);
    }

    public Post updatePost(Long postId, Post updated) {
        Post post = getPost(postId); // 이미 PostNotFoundException 처리됨
        post.setTitle(updated.getTitle());
        post.setDescription(updated.getDescription());
        post.setPrice(updated.getPrice());
        post.setStatus(updated.getStatus());
        return postRepository.save(post);
    }

    public void deletePost(Long postId) {
        Post post = getPost(postId); // 이미 PostNotFoundException 처리됨
        postRepository.delete(post);
    }

    public void incrementViews(Long postId) {
        Post post = getPost(postId); // 이미 PostNotFoundException 처리됨
        post.setViews(post.getViews() + 1);
        postRepository.save(post);
    }
}