package com.ssafy.spring_boot.chat.repository;

import com.ssafy.spring_boot.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom,Integer> {
    List<ChatRoom> findAllByBuyer_Id(Long buyerId);
}
