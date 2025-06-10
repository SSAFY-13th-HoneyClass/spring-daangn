package com.ssafy.springdaangn.Exception;

public class LikeNotFoundException extends RuntimeException {
    public LikeNotFoundException(String message) {
        super(message);
    }

    public LikeNotFoundException(Long userId, Long postId) {
        super("좋아요를 찾을 수 없습니다. 사용자 ID: " + userId + ", 게시글 ID: " + postId);
    }
}