package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}

