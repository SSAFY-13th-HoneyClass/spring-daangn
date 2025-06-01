package com.ssafy.daangn.comment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.daangn.comment.dto.request.CommentRequestDto;
import com.ssafy.daangn.comment.dto.response.CommentResponseDto;
import com.ssafy.daangn.comment.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "댓글 관련 API")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 생성", description = "게시글에 새로운 댓글 또는 대댓글을 작성합니다.")
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@RequestBody CommentRequestDto dto) {
        return ResponseEntity.ok(commentService.createComment(dto));
    }

    @Operation(summary = "게시글 댓글 전체 조회", description = "특정 게시글에 대한 모든 댓글을 조회합니다. (삭제되지 않은 것만)")
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(commentService.getCommentsByBoard(boardId));
    }

    @Operation(summary = "게시글의 최상위 댓글 조회", description = "게시글 내 부모 댓글이 없는 최상위 댓글을 조회합니다.")
    @GetMapping("/board/{boardId}/root")
    public ResponseEntity<List<CommentResponseDto>> getRootCommentsByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(commentService.getRootCommentsByBoard(boardId));
    }

    @Operation(summary = "대댓글 조회", description = "부모 댓글 ID를 통해 대댓글을 조회합니다.")
    @GetMapping("/parent/{parentCommentId}")
    public ResponseEntity<List<CommentResponseDto>> getChildComments(@PathVariable Long parentCommentId) {
        return ResponseEntity.ok(commentService.getChildComments(parentCommentId));
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 soft delete 처리합니다.")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "댓글 수정", description = "기존 댓글 내용을 수정합니다.")
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto dto) {

        CommentResponseDto updated = commentService.updateComment(commentId, dto);
        return ResponseEntity.ok(updated);
    }

}
