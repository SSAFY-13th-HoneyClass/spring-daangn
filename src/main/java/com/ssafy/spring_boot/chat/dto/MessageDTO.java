package com.ssafy.spring_boot.chat.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageDTO {
    private Long id;               // 메시지 ID
    private Long chatRoomId;       // 채팅방 ID
    private Long senderId;         // 보낸 사용자 ID
    private String senderNickname; // 보낸 사용자 닉네임
    private String message;        // 메시지 본문
    private LocalDateTime sendAt;  // 전송 시간
    private Boolean isRead;        // 읽음 여부
}
