package com.example.daangn.domain.chat.repository;

import com.example.daangn.domain.chat.entity.Chatting;
import com.example.daangn.domain.chat.entity.ChattingLog;
import com.example.daangn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChattingLogRepository extends JpaRepository<ChattingLog, Long> {
    List<ChattingLog> findByChatting(Chatting chatting);
    List<ChattingLog> findByChattingAndCheckFalseAndUserNot(Chatting chatting, User user);
    long countByChattingAndCheckFalse(Chatting chatting);
}
