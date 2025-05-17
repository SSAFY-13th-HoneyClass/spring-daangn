package com.ssafy.springdaangn.service;

import com.ssafy.springdaangn.Domain.ChatMessage;
import com.ssafy.springdaangn.Domain.ChatRoom;
import com.ssafy.springdaangn.Repository.ChatMessageRepository;
import com.ssafy.springdaangn.Repository.ChatRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {
    private final ChatMessageRepository chatmessageRepository;
    private final ChatRoomRepository chatroomRepository;

    public ChatMessage sendMessage(Long roomId, ChatMessage message) {
        ChatRoom room = chatroomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Chatroom not found: " + roomId));
        message.setChatRoom(room);
        return chatmessageRepository.save(message);
    }

    public List<ChatMessage> getMessages(Long roomId) {
        return chatmessageRepository.findByChatRoom_ChatRoomIdOrderByTimeAsc(roomId);
    }

    public void markAsRead(Long messageId) {
        ChatMessage msg = chatmessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found: " + messageId));
        msg.setIsRead (true);
        chatmessageRepository.save(msg);
    }
}
