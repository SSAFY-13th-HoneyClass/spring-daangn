package com.ssafy.daangn.boardphoto.dto.response;

import java.time.LocalDateTime;

import com.ssafy.daangn.boardphoto.entity.BoardPhoto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardPhotoResponseDto {

    @Schema(description = "사진 ID", example = "1")
    private Long photoId;

    @Schema(description = "게시글 ID", example = "1")
    private Long boardId;

    @Schema(description = "사진 URL", example = "https://example.com/image.jpg")
    private String url;

    @Schema(description = "생성 시각", example = "2025-06-01T12:34:56")
    private LocalDateTime createdAt;

    public static BoardPhotoResponseDto from(BoardPhoto photo) {
        return BoardPhotoResponseDto.builder()
                .photoId(photo.getPhotoId())
                .boardId(photo.getBoard().getBoardId())
                .url(photo.getUrl())
                .createdAt(photo.getCreatedAt())
                .build();
    }
}