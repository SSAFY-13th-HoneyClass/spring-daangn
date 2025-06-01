package com.ssafy.daangn.directmessage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.daangn.directmessage.dto.request.DirectMessageRequestDto;
import com.ssafy.daangn.directmessage.dto.response.DirectMessageResponseDto;
import com.ssafy.daangn.directmessage.service.DirectMessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/dm")
@RequiredArgsConstructor
@Tag(name = "DirectMessage", description = "DM 관련 API")
public class DirectMessageController {

    private final DirectMessageService dmService;

    @Operation(summary = "DM 전송", description = "게시글 기반 DM 방에 쪽지를 전송합니다.")
    @PostMapping("/board/{boardId}")
    public ResponseEntity<DirectMessageResponseDto> sendMessage(
            @PathVariable Long boardId, @RequestBody DirectMessageRequestDto dto) {
        return ResponseEntity.ok(dmService.sendMessage(boardId, dto));
    }

    @Operation(summary = "DM 목록 조회", description = "특정 DM 방의 메시지 목록을 조회합니다.")
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<DirectMessageResponseDto>> getMessages(@PathVariable Long roomId) {
        return ResponseEntity.ok(dmService.getMessagesInRoom(roomId));
    }
}