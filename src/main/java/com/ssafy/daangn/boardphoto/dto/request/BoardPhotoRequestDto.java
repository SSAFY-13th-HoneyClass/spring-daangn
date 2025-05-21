package com.ssafy.daangn.boardphoto.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardPhotoRequestDto {
    private Long boardId;
    private String url;
    private Integer photoOrder;
}
