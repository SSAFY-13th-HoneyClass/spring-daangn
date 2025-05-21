package com.ssafy.daangn.comment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentRequestDto {
    private Long boardId;
    private Long memberId;
    private Long parentCommentId; // nullable
    private String content;
}