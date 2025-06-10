package com.ssafy.springdaangn.service;

import com.ssafy.springdaangn.Domain.ChatMessage;
import com.ssafy.springdaangn.Domain.ChatRoom;
import com.ssafy.springdaangn.Exception.ChatMessageNotFoundException;
import com.ssafy.springdaangn.Exception.ChatRoomNotFoundException;
import com.ssafy.springdaangn.Repository.ChatMessageRepository;
import com.ssafy.springdaangn.Repository.ChatRoomRepository;

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