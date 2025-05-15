package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findTop100ByChatRoomNoOrderByCreatedAtAsc(Long chatRoomNo);
    void deleteByChatRoomNo(Long chatRoomNo);
}

