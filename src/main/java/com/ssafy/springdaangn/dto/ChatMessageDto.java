package com.ssafy.springdaangn.dto;

import com.ssafy.springdaangn.domain.ChatMessage;
import com.ssafy.springdaangn.domain.MessageType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageDto {
    private Long chatMessageId;
    private String message;
    private Boolean isRead;
    private MessageType messageType;
    private LocalDateTime time;
    private Long chatRoomId;

    // 정적 팩토리 메서드 - Entity -> DTO
    public static ChatMessageDto from(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
                .chatMessageId(chatMessage.getChatMessageId())
                .message(chatMessage.getMessage())
                .isRead(chatMessage.getIsRead())
                .messageType(chatMessage.getMessageType())
                .time(chatMessage.getTime())
                .chatRoomId(chatMessage.getChatRoom() != null ?
                        chatMessage.getChatRoom().getChatRoomId() : null)
                .build();
    }

    // 생성용 요청 DTO
    @Getter
    @Builder
    public static class CreateRequest {
        private String message;
        private MessageType messageType;

        public static CreateRequest of(String message, MessageType messageType) {
            return CreateRequest.builder()
                    .message(message)
                    .messageType(messageType != null ? messageType : MessageType.TEXT)
                    .build();
        }

        public static CreateRequest ofText(String message) {
            return CreateRequest.builder()
                    .message(message)
                    .messageType(MessageType.TEXT)
                    .build();
        }

        public ChatMessage toEntity() {
            return ChatMessage.builder()
                    .message(this.message)
                    .messageType(this.messageType)
                    .isRead(false)
                    .time(LocalDateTime.now())
                    .build();
        }
    }

    // 목록용 간단한 DTO
    @Getter
    @Builder
    public static class Summary {
        private Long chatMessageId;
        private String message;
        private Boolean isRead;
        private LocalDateTime time;

        public static Summary from(ChatMessage chatMessage) {
            return Summary.builder()
                    .chatMessageId(chatMessage.getChatMessageId())
                    .message(chatMessage.getMessage())
                    .isRead(chatMessage.getIsRead())
                    .time(chatMessage.getTime())
                    .build();
        }
    }
}