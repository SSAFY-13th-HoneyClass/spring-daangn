package com.ssafy.daangn.comment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.daangn.comment.dto.request.CommentRequestDto;
import com.ssafy.daangn.comment.dto.response.CommentResponseDto;
import com.ssafy.daangn.comment.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@RequestBody CommentRequestDto dto) {
        return ResponseEntity.ok(commentService.createComment(dto));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(commentService.getCommentsByBoard(boardId));
    }

    @GetMapping("/board/{boardId}/roots")
    public ResponseEntity<List<CommentResponseDto>> getRootComments(@PathVariable Long boardId) {
        return ResponseEntity.ok(commentService.getRootCommentsByBoard(boardId));
    }

    @GetMapping("/parent/{parentCommentId}")
    public ResponseEntity<List<CommentResponseDto>> getChildComments(@PathVariable Long parentCommentId) {
        return ResponseEntity.ok(commentService.getChildComments(parentCommentId));
    }
}
