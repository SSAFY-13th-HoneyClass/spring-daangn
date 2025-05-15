package com.ssafy.spring_boot.comment.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentDTO {
    private Long id;                // 댓글 ID
    private Long productId;         // 상품 ID
    private Long userId;            // 작성자 ID
    private String userNickname;    // 작성자 닉네임
    private String content;         // 댓글 내용
    private LocalDateTime createAt; // 작성일시
    private Long parentCommentId;   // 부모 댓글 ID
    private Integer childCount;     // 대댓글 수
    private Integer level;          // 댓글 계층 깊이
    private String hierarchyPath;   // 계층 정렬 경로
}
