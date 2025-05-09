// Chatting Repository
package org.example.springboot.repository;

import org.example.springboot.entity.Chatting;
import org.example.springboot.entity.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChattingRepository extends JpaRepository<Chatting, Long> {
    List<Chatting> findByChattingRoom(ChattingRoom chattingRoom);

    @Query("SELECT c FROM Chatting c WHERE c.chattingRoom.chattingRoomId = :roomId ORDER BY c.createdAt ASC")
    List<Chatting> findByChattingRoomIdOrderByCreatedAtAsc(@Param("roomId") Long roomId);
}