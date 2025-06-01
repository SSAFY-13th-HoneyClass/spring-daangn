package com.ssafy.daangn.directmessage.dto.response;

import java.time.LocalDateTime;

import com.ssafy.daangn.directmessage.entity.DirectMessage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DirectMessageResponseDto {
    private Long messageId;
    private Long roomId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isRead;

    public static DirectMessageResponseDto from(DirectMessage dm) {
        return DirectMessageResponseDto.builder()
                .messageId(dm.getMessageId())
                .roomId(dm.getRoom().getRoomId())
                .senderId(dm.getSender().getMemberId())
                .receiverId(dm.getReceiver().getMemberId())
                .content(dm.getContent())
                .createdAt(dm.getCreatedAt())
                .isRead(dm.getIsRead())
                .build();
    }
}