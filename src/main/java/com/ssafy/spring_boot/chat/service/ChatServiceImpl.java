package com.ssafy.spring_boot.chat.service;

import com.ssafy.spring_boot.chat.domain.ChatRoom;
import com.ssafy.spring_boot.chat.domain.Message;
import com.ssafy.spring_boot.chat.dto.MessageDTO;
import com.ssafy.spring_boot.chat.repository.ChatRoomRepository;
import com.ssafy.spring_boot.chat.repository.MessageRepository;
import com.ssafy.spring_boot.chat.service.ChatService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public List<MessageDTO> getMessagesByRoomId(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다. ID: " + roomId));

        List<Message> messages = messageRepository.findAllByChatRoom(chatRoom);

        return messages.stream()
                .map(MessageDTO::from)
                .collect(Collectors.toList());
    }
}