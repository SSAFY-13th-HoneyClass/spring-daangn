package com.ssafy.daangn.comment.dto.response;

import java.time.LocalDateTime;

import com.ssafy.daangn.comment.entity.Comment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponseDto {
    private Long commentId;
    private Long boardId;
    private Long memberId;
    private Long parentCommentId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponseDto fromEntity(Comment comment) {
        return CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .boardId(comment.getBoard().getBoardId())
                .memberId(comment.getMember().getMemberId())
                .parentCommentId(
                    comment.getParent() != null ? comment.getParent().getCommentId() : null
                )
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}