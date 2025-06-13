package org.example.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.springboot.domain.Post;

import java.time.LocalDateTime;

public class ItemDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "아이템 생성 요청 DTO")
    public static class CreateRequest {
        @Schema(description = "아이템 제목", example = "아이폰 14 팝니다", required = true)
        private String title;
        
        @Schema(description = "아이템 설명", example = "깨끗하게 사용했습니다. 직거래 선호", required = true)
        private String content;

        // 정적 팩토리 메서드
        public static CreateRequest of(String title, String content) {
            return CreateRequest.builder()
                    .title(title)
                    .content(content)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "아이템 목록 조회 응답 DTO")
    public static class ListResponse {
        @Schema(description = "아이템 ID", example = "1")
        private Long itemId;
        
        @Schema(description = "아이템 제목", example = "아이폰 14 팝니다")
        private String title;
        
        @Schema(description = "아이템 상태", example = "AVAILABLE")
        private String status;
        
        @Schema(description = "사용자 ID", example = "1")
        private Long userId;
        
        @Schema(description = "사용자 닉네임", example = "사용자닉네임")
        private String nickname;
        
        @Schema(description = "생성일시", example = "2024-01-01T10:00:00")
        private LocalDateTime createdAt;

        // 정적 팩토리 메서드
        public static ListResponse from(Post post) {
            return ListResponse.builder()
                    .itemId(post.getPostId())
                    .title(post.getTitle())
                    .status(post.getStatus())
                    .userId(post.getUser().getUserId())
                    .nickname(post.getUser().getNickname())
                    .createdAt(post.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "아이템 상세 조회 응답 DTO")
    public static class DetailResponse {
        @Schema(description = "아이템 ID", example = "1")
        private Long itemId;
        
        @Schema(description = "아이템 제목", example = "아이폰 14 팝니다")
        private String title;
        
        @Schema(description = "아이템 설명", example = "깨끗하게 사용했습니다. 직거래 선호")
        private String content;
        
        @Schema(description = "아이템 상태", example = "AVAILABLE")
        private String status;
        
        @Schema(description = "사용자 ID", example = "1")
        private Long userId;
        
        @Schema(description = "사용자 닉네임", example = "사용자닉네임")
        private String nickname;
        
        @Schema(description = "프로필 이미지 경로", example = "/images/profile.jpg")
        private String profileImgPath;
        
        @Schema(description = "생성일시", example = "2024-01-01T10:00:00")
        private LocalDateTime createdAt;
        
        @Schema(description = "수정일시", example = "2024-01-01T10:30:00")
        private LocalDateTime updatedAt;

        // 정적 팩토리 메서드
        public static DetailResponse from(Post post) {
            return DetailResponse.builder()
                    .itemId(post.getPostId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .status(post.getStatus())
                    .userId(post.getUser().getUserId())
                    .nickname(post.getUser().getNickname())
                    .profileImgPath(post.getUser().getProfileImgPath())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "아이템 업데이트 요청 DTO")
    public static class UpdateRequest {
        @Schema(description = "아이템 제목", example = "아이폰 14 급처분")
        private String title;
        
        @Schema(description = "아이템 설명", example = "가격 네고 가능합니다")
        private String content;
        
        @Schema(description = "아이템 상태", example = "RESERVED", allowableValues = {"AVAILABLE", "RESERVED", "SOLD"})
        private String status;

        // 정적 팩토리 메서드
        public static UpdateRequest of(String title, String content, String status) {
            return UpdateRequest.builder()
                    .title(title)
                    .content(content)
                    .status(status)
                    .build();
        }
    }
} 