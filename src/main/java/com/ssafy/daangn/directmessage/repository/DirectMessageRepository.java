package com.ssafy.daangn.directmessage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.daangn.directmessage.entity.DirectMessage;
import com.ssafy.daangn.directmessageroom.entity.DirectMessageRoom;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {
    List<DirectMessage> findByRoomOrderByCreatedAtAsc(DirectMessageRoom room);
}