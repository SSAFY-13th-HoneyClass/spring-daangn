package com.ssafy.spring_boot.chat.dto;

import com.ssafy.spring_boot.chat.domain.Message;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageDTO {
    private Long id;
    private Long chatRoomId;
    private Long senderId;
    private String senderNickname;
    private String message;
    private LocalDateTime sendAt;
    private Boolean isRead;

    // Entity -> DTO 변환 메소드
    public static MessageDTO from(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoom().getId())
                .senderId(message.getSender().getId().longValue())
                .senderNickname(message.getSender().getNickname())
                .message(message.getMessage())
                .sendAt(message.getSendAt())
                .isRead(message.getIsRead())
                .build();
    }
}