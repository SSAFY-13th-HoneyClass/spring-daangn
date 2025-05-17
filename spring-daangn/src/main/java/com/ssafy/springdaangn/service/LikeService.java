package com.ssafy.springdaangn.service;

import com.ssafy.springdaangn.Domain.Like;
import com.ssafy.springdaangn.Domain.Post;
import com.ssafy.springdaangn.Domain.User;
import com.ssafy.springdaangn.Repository.LikeRepository;
import com.ssafy.springdaangn.Repository.PostRepository;
import com.ssafy.springdaangn.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void likePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));

        if (likeRepository.existsByUser_UserIdAndPost_PostId(userId, postId)) {
            throw new IllegalStateException("Already liked");
        }
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        likeRepository.save(like);

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    public void unlikePost(Long userId, Long postId) {
        Like like = (Like) likeRepository.findByUser_UserIdAndPost_PostId(userId, postId)
                .orElseThrow(() -> new EntityNotFoundException("Like not found"));
        Post post = like.getPost();
        likeRepository.delete(like);

        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        postRepository.save(post);
    }
}
