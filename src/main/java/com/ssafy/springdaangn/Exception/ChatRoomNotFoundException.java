package com.ssafy.springdaangn.Exception;

public class ChatRoomNotFoundException extends RuntimeException {
    public ChatRoomNotFoundException(String message) {
        super(message);
    }

    public ChatRoomNotFoundException(Long roomId) {
        super("채팅방을 찾을 수 없습니다. ID: " + roomId);
    }
}