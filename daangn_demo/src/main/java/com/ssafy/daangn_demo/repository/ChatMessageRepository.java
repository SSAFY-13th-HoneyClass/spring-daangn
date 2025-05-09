package com.ssafy.daangn_demo.repository;

import com.ssafy.daangn_demo.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
}
