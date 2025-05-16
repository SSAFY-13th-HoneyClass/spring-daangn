package com.ssafy.spring_boot.comment.dto;

import com.ssafy.spring_boot.comment.domain.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentDTO {
    private Long id;
    private Long productId;
    private Long userId;
    private String userNickname;
    private String content;
    private LocalDateTime createAt;
    private Long parentCommentId;
    private Integer childCount;
    private Integer level;
    private String hierarchyPath;

    // Entity -> DTO 변환 메소드
    public static CommentDTO from(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .productId(comment.getProduct().getId())
                .userId(comment.getUser().getId().longValue())
                .userNickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .createAt(comment.getCreateAt())
                .parentCommentId(comment.getParentCommentId())
                .childCount(comment.getChildCount())
                .level(comment.getLevel())
                .hierarchyPath(comment.getHierarchyPath())
                .build();
    }
}