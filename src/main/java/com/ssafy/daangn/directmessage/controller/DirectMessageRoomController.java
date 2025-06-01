package com.ssafy.daangn.directmessage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.daangn.directmessage.dto.response.DirectMessageRoomResponseDto;
import com.ssafy.daangn.directmessage.service.DirectMessageRoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/dm/room")
@RequiredArgsConstructor
@Tag(name = "DirectMessageRoom", description = "DM 방 관련 API")
public class DirectMessageRoomController {

    private final DirectMessageRoomService roomService;

    @Operation(summary = "특정 게시글의 나의 DM 방 목록", description = "특정 게시글에서 로그인한 사용자가 참여한 DM 방 목록을 조회합니다.")
    @GetMapping("/board/{boardId}/member/{memberId}")
    public ResponseEntity<List<DirectMessageRoomResponseDto>> getRoomsForBoard(
            @PathVariable Long boardId,
            @PathVariable Long memberId) {
        return ResponseEntity.ok(roomService.getRoomsForBoard(boardId, memberId));
    }

    @Operation(summary = "1:1 DM 방 단일 조회 또는 생성", description = "게시글 ID, 송신자 ID, 수신자 ID를 기반으로 1:1 DM 방을 조회하거나 없으면 생성합니다.")
    @GetMapping("/board/{boardId}/sender/{senderId}/receiver/{receiverId}")
    public ResponseEntity<DirectMessageRoomResponseDto> getOrCreateRoom(
            @PathVariable Long boardId,
            @PathVariable Long senderId,
            @PathVariable Long receiverId) {
        return ResponseEntity.ok(roomService.getOrCreateRoom(boardId, senderId, receiverId));
    }

    @Operation(summary = "내가 참여한 전체 DM 방 조회", description = "현재 로그인한 사용자가 참여한 모든 DM 방을 조회합니다.")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<DirectMessageRoomResponseDto>> getRoomsByMember(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(roomService.getRoomsByMember(memberId));
    }
}
