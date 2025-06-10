package com.ssafy.springdaangn.service;

import com.ssafy.springdaangn.Domain.Like;
import com.ssafy.springdaangn.Domain.Post;
import com.ssafy.springdaangn.Domain.User;
import com.ssafy.springdaangn.Exception.AlreadyLikedException;
import com.ssafy.springdaangn.Exception.LikeNotFoundException;
import com.ssafy.springdaangn.Exception.PostNotFoundException;
import com.ssafy.springdaangn.Exception.UserNotFoundException;
import com.ssafy.springdaangn.Repository.LikeRepository;
import com.ssafy.springdaangn.Repository.PostRepository;
import com.ssafy.springdaangn.Repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void likePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (likeRepository.existsByUser_UserIdAndPost_PostId(userId, postId)) {
            throw new AlreadyLikedException(userId, postId);
        }

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        likeRepository.save(like);

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    public void unlikePost(Long userId, Long postId) {
        Like like = likeRepository.findByUser_UserIdAndPost_PostId(userId, postId)
                .orElseThrow(() -> new LikeNotFoundException(userId, postId));
        Post post = like.getPost();
        likeRepository.delete(like);

        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        postRepository.save(post);
    }
}