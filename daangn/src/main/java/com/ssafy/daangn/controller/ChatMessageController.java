
// ChatMessageController.java (WebSocket 메시지는 별도 처리가 필요하지만 기본적인 HTTP API)
package com.ssafy.daangn.controller;

import com.ssafy.daangn.domain.ChatMessage;
import com.ssafy.daangn.repository.ChatMessageRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/message")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ChatMessage", description = "채팅 메시지 관리 API")
public class ChatMessageController {

    private final ChatMessageRepository chatMessageRepository;

    @Operation(summary = "채팅 메시지 조회", description = "특정 채팅방의 메시지 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음")
    })
    @GetMapping("/room/{chatRoomNo}")
    public ResponseEntity<List<ChatMessage>> getChatMessages(
            @Parameter(description = "채팅방 번호") @PathVariable Long chatRoomNo) {

        List<ChatMessage> messages = chatMessageRepository.findTop100ByChatRoomNoOrderByCreatedAtAsc(chatRoomNo);
        return ResponseEntity.ok(messages);
    }
}