package com.ssafy.daangn.board.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.daangn.board.dto.request.BoardRequestDto;
import com.ssafy.daangn.board.dto.response.BoardResponseDto;
import com.ssafy.daangn.board.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(@RequestBody BoardRequestDto dto) {
        return ResponseEntity.ok(boardService.createBoard(dto));
    }

    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoards() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

    @GetMapping("/active")
    public ResponseEntity<List<BoardResponseDto>> getActiveBoards() {
        return ResponseEntity.ok(boardService.getActiveBoards());
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<BoardResponseDto>> getBoardsByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(boardService.getBoardsByMember(memberId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BoardResponseDto>> searchBoards(@RequestParam String keyword) {
        return ResponseEntity.ok(boardService.searchBoardsByTitle(keyword));
    }
}
