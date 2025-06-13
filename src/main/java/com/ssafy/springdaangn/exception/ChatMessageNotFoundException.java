package com.ssafy.springdaangn.exception;

public class ChatMessageNotFoundException extends RuntimeException {
    public ChatMessageNotFoundException(String message) {
        super(message);
    }

    public ChatMessageNotFoundException(Long messageId) {
        super("메시지를 찾을 수 없습니다. ID: " + messageId);
    }
}