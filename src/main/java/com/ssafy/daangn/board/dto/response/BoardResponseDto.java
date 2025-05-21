package com.ssafy.daangn.board.dto.response;

import java.time.LocalDateTime;

import com.ssafy.daangn.board.entity.Board;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardResponseDto {

    private Long boardId;
    private Long memberId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isDeleted;

    public static BoardResponseDto from(Board board) {
        return BoardResponseDto.builder()
                .boardId(board.getBoardId())
                .memberId(board.getMember().getMemberId())
                .title(board.getTitle())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .isDeleted(board.getIsDeleted())
                .build();
    }
}
