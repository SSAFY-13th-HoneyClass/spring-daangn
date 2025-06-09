package com.ssafy.daangn.boardphoto.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardPhotoRequestDto {

    @Schema(description = "게시글 ID", example = "1")
    private Long boardId;

    @Schema(description = "사진 URL", example = "https://example.com/image.jpg")
    private String url;
}