package com.ssafy.springdaangn.controller;

import com.ssafy.springdaangn.Domain.ChatRoom;
import com.ssafy.springdaangn.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatRoomController {

    private final ChatroomService chatroomService;

    // 1. 채팅방 개설
    @PostMapping
    public ResponseEntity<ChatRoom> openChatroom(
            @RequestParam Long postId,
            @RequestParam Long buyerId) {
        ChatRoom chatRoom = chatroomService.openChatroom(postId, buyerId);
        return new ResponseEntity<>(chatRoom, HttpStatus.CREATED);
    }

    // 2. 특정 채팅방 조회
    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoom> getChatroom(@PathVariable Long roomId) {
        ChatRoom chatRoom = chatroomService.getChatroom(roomId);
        return ResponseEntity.ok(chatRoom);
    }

    // 3. 특정 사용자의 채팅방 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatRoom>> getChatroomsByUser(@PathVariable Long userId) {
        List<ChatRoom> chatRooms = chatroomService.getChatroomsByUser(userId);
        return ResponseEntity.ok(chatRooms);
    }

    // 4. 채팅방 닫기
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> closeChatroom(@PathVariable Long roomId) {
        chatroomService.closeChatroom(roomId);
        return ResponseEntity.noContent().build();
    }
}