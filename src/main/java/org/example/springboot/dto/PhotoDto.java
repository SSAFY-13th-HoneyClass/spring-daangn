package org.example.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.springboot.domain.Photo;

import java.time.LocalDateTime;

public class PhotoDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "사진 업로드 요청 DTO")
    public static class UploadRequest {
        @Schema(description = "파일 경로", example = "/images/photo.jpg", required = true)
        private String path;
        
        @Schema(description = "게시글 ID", example = "1", required = true)
        private Long postId;

        // 정적 팩토리 메서드
        public static UploadRequest of(String path, Long postId) {
            return UploadRequest.builder()
                    .path(path)
                    .postId(postId)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "사진 정보 응답 DTO")
    public static class Response {
        @Schema(description = "이미지 ID", example = "1")
        private Long imageId;
        
        @Schema(description = "파일 경로", example = "/images/photo.jpg")
        private String path;
        
        @Schema(description = "게시글 ID", example = "1")
        private Long postId;
        
        @Schema(description = "생성일시", example = "2024-01-01T10:00:00")
        private LocalDateTime createdAt;

        // 정적 팩토리 메서드
        public static Response from(Photo photo) {
            return Response.builder()
                    .imageId(photo.getImageId())
                    .path(photo.getPath())
                    .postId(photo.getPost().getPostId())
                    .createdAt(photo.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "사진 업로드 성공 응답 DTO")
    public static class UploadResponse {
        @Schema(description = "이미지 ID", example = "1")
        private Long imageId;
        
        @Schema(description = "파일 경로", example = "/images/photo.jpg")
        private String path;
        
        @Schema(description = "응답 메시지", example = "사진 업로드 성공")
        private String message;

        // 정적 팩토리 메서드
        public static UploadResponse of(Long imageId, String path, String message) {
            return UploadResponse.builder()
                    .imageId(imageId)
                    .path(path)
                    .message(message)
                    .build();
        }
    }
}