package com.ssafy.daangn.board.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.daangn.board.dto.request.BoardRequestDto;
import com.ssafy.daangn.board.dto.response.BoardResponseDto;
import com.ssafy.daangn.board.service.BoardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
@Tag(name = "Board", description = "게시글 관련 API")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 등록합니다.")
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(@RequestBody BoardRequestDto dto) {
        return ResponseEntity.ok(boardService.createBoard(dto));
    }

    @Operation(summary = "전체 게시글 조회", description = "삭제되지 않은 활성 게시글만 조회합니다.")
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoards() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

    @Operation(summary = "삭제된 게시글 조회", description = "삭제 처리된 게시글만 조회합니다.")
    @GetMapping("/deleted")
    public ResponseEntity<List<BoardResponseDto>> getDeletedBoards() {
        return ResponseEntity.ok(boardService.getDeletedBoards());
    }

    @Operation(summary = "회원의 게시글 조회", description = "특정 회원이 작성한 게시글 목록을 조회합니다.")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<BoardResponseDto>> getBoardsByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(boardService.getBoardsByMember(memberId));
    }

    @Operation(summary = "게시글 제목 검색", description = "키워드를 포함한 게시글 제목으로 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<BoardResponseDto>> searchBoards(@RequestParam String keyword) {
        return ResponseEntity.ok(boardService.searchBoardsByTitle(keyword));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 소프트 삭제 처리합니다.")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글 수정", description = "게시글의 제목과 내용을 수정합니다.")
    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable Long boardId, @RequestBody BoardRequestDto dto) {
        return ResponseEntity.ok(boardService.updateBoard(boardId, dto));
    }
}
