package com.ssafy.daangn.dto;

import com.ssafy.daangn.domain.BaseEntity;
import com.ssafy.daangn.domain.ChatRoom;
import com.ssafy.daangn.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto{
    private Long no;
    private String message;
    private Boolean isRead;
    private ChatRoom chatRoom;
    private Long writerNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
