package com.ssafy.springdaangn.repository;

import com.ssafy.springdaangn.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByPostPostId(Long postId);
    List<ChatRoom> findBySellerUserIdOrBuyerUserId(Long sellerId, Long buyerId);
}