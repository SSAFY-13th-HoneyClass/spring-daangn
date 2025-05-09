package com.ssafy.daangn_demo.repository;

import com.ssafy.daangn_demo.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
}
