package com.ssafy.daangn.boardphoto.dto.response;

import com.ssafy.daangn.boardphoto.entity.BoardPhoto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardPhotoResponseDto {
    private Long photoId;
    private Long boardId;
    private String url;
    private Integer photoOrder;

    public static BoardPhotoResponseDto from(BoardPhoto photo) {
        return BoardPhotoResponseDto.builder()
                .photoId(photo.getPhotoId())
                .boardId(photo.getBoard().getBoardId())
                .url(photo.getUrl())
                .photoOrder(photo.getPhotoOrder())
                .build();
    }
}
