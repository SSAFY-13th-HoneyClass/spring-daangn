package com.ssafy.springdaangn.controller;

import com.ssafy.springdaangn.Domain.ChatMessage;
import com.ssafy.springdaangn.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    // 1. 메시지 전송
    @PostMapping
    public ResponseEntity<ChatMessage> sendMessage(
            @RequestParam Long roomId,
            @RequestBody ChatMessage message) {
        ChatMessage sentMessage = chatMessageService.sendMessage(roomId, message);
        return new ResponseEntity<>(sentMessage, HttpStatus.CREATED);
    }

    // 2. 특정 채팅방의 메시지 목록 조회
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long roomId) {
        List<ChatMessage> messages = chatMessageService.getMessages(roomId);
        return ResponseEntity.ok(messages);
    }

    // 3. 메시지 읽음 처리
    @PatchMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long messageId) {
        chatMessageService.markAsRead(messageId);
        return ResponseEntity.ok().build();
    }
}