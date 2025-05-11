package com.ssafy.spring_boot.chat.repository;

import com.ssafy.spring_boot.chat.domain.ChatRoom;
import com.ssafy.spring_boot.chat.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Integer> {
    List<Message> findAllByChatRoom(ChatRoom chatRoom);
}
