package com.ssafy.daangn.favorite.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRequestDto {
    private Long memberId;
    private Long boardId;
}
