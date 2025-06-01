package org.example.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.springboot.domain.Comment;

import java.time.LocalDateTime;

public class CommentDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "댓글 생성 요청 DTO")
    public static class CreateRequest {
        @Schema(description = "댓글 내용", example = "좋은 상품이네요!", required = true)
        private String content;
        
        @Schema(description = "게시글 ID", example = "1", required = true)
        private Long postId;
        
        @Schema(description = "사용자 ID", example = "1", required = true)
        private Long userId;

        // 정적 팩토리 메서드
        public static CreateRequest of(String content, Long postId, Long userId) {
            return CreateRequest.builder()
                    .content(content)
                    .postId(postId)
                    .userId(userId)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "댓글 응답 DTO")
    public static class Response {
        @Schema(description = "댓글 ID", example = "1")
        private Long commentId;
        
        @Schema(description = "댓글 내용", example = "좋은 상품이네요!")
        private String content;
        
        @Schema(description = "작성자 정보")
        private UserDto.UserResponse user;
        
        @Schema(description = "게시글 ID", example = "1")
        private Long postId;
        
        @Schema(description = "생성일시", example = "2024-01-01T10:00:00")
        private LocalDateTime createdAt;

        // 정적 팩토리 메서드
        public static Response from(Comment comment) {
            return Response.builder()
                    .commentId(comment.getCommentId())
                    .content(comment.getContent())
                    .user(UserDto.UserResponse.from(comment.getUser()))
                    .postId(comment.getPost().getPostId())
                    .createdAt(comment.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "댓글 업데이트 요청 DTO")
    public static class UpdateRequest {
        @Schema(description = "댓글 내용", example = "수정된 댓글 내용입니다.", required = true)
        private String content;

        // 정적 팩토리 메서드
        public static UpdateRequest of(String content) {
            return UpdateRequest.builder()
                    .content(content)
                    .build();
        }
    }
}