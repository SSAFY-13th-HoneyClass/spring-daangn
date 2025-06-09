package com.ssafy.daangn.directmessageroom.dto.response;

import java.time.LocalDateTime;

import com.ssafy.daangn.directmessageroom.entity.DirectMessageRoom;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DirectMessageRoomResponseDto {
    private Long roomId;
    private Long boardId;
    private Long senderId;
    private Long receiverId;
    private LocalDateTime createdAt;

    public static DirectMessageRoomResponseDto from(DirectMessageRoom room) {
        return DirectMessageRoomResponseDto.builder()
                .roomId(room.getRoomId())
                .boardId(room.getBoard().getBoardId())
                .senderId(room.getSender().getMemberId())
                .receiverId(room.getReceiver().getMemberId())
                .createdAt(room.getCreatedAt())
                .build();
    }
}