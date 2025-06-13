package com.ssafy.springdaangn.service;

import com.ssafy.springdaangn.domain.ChatMessage;
import com.ssafy.springdaangn.domain.ChatRoom;
import com.ssafy.springdaangn.exception.ChatMessageNotFoundException;
import com.ssafy.springdaangn.exception.ChatRoomNotFoundException;
import com.ssafy.springdaangn.repository.ChatMessageRepository;
import com.ssafy.springdaangn.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {
    private final ChatMessageRepository chatmessageRepository;
    private final ChatRoomRepository chatroomRepository;

    public ChatMessage sendMessage(Long roomId, ChatMessage message) {
        ChatRoom room = chatroomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(roomId));
        message.setChatRoom(room);
        return chatmessageRepository.save(message);
    }

    public List<ChatMessage> getMessages(Long roomId) {
        // 채팅방 존재 여부 확인
        chatroomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(roomId));
        return chatmessageRepository.findByChatRoom_ChatRoomIdOrderByTimeAsc(roomId);
    }

    public void markAsRead(Long messageId) {
        ChatMessage msg = chatmessageRepository.findById(messageId)
                .orElseThrow(() -> new ChatMessageNotFoundException(messageId));
        msg.setIsRead(true);
        chatmessageRepository.save(msg);
    }
}