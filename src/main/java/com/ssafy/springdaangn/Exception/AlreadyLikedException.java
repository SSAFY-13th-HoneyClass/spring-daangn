package com.ssafy.springdaangn.Exception;

public class AlreadyLikedException extends RuntimeException {
    public AlreadyLikedException(String message) {
        super(message);
    }

    public AlreadyLikedException(Long userId, Long postId) {
        super("이미 좋아요를 누른 게시글입니다. 사용자 ID: " + userId + ", 게시글 ID: " + postId);
    }
}