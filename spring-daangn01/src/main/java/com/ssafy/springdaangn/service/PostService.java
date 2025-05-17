package com.ssafy.springdaangn.service;

import com.ssafy.springdaangn.Domain.Post;
import com.ssafy.springdaangn.Domain.User;
import com.ssafy.springdaangn.Repository.PostRepository;
import com.ssafy.springdaangn.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Post createPost(Long userId, Post post) {
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        post.setSeller(seller);
        return postRepository.save(post);
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));
    }

    public List<Post> getPostsByUser(Long userId) {
        return postRepository.findBySellerUserId(userId);
    }

//    public List<Post> getPostsByNeighborhood(Long neighborhoodId) {
//        return postRepository.findByNeighborhood(neighborhoodId);
//    }

    public Post updatePost(Long postId, Post updated) {
        Post post = getPost(postId);
        post.setTitle(updated.getTitle());
        post.setDescription(updated.getDescription());
        post.setPrice(updated.getPrice());
        post.setStatus(updated.getStatus());
        // 기타 필드 업데이트
        return postRepository.save(post);
    }

    public void deletePost(Long postId) {
        Post post = getPost(postId);
        postRepository.delete(post);
    }

    public void incrementViews(Long postId) {
        Post post = getPost(postId);
        post.setViews(post.getViews() + 1);
        postRepository.save(post);
    }
}
