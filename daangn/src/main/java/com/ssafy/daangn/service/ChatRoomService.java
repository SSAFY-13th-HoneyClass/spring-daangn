package com.ssafy.daangn.service;

import com.ssafy.daangn.domain.ChatRoom;
import com.ssafy.daangn.dto.ChatRoomDto;
import com.ssafy.daangn.dto.ChatRoomResponseDto;

import java.util.List;

public interface ChatRoomService {

    ChatRoomResponseDto save(ChatRoomDto chatRoom);

    void delete(ChatRoomDto chatRoom);

    List<ChatRoomResponseDto> findBySellerNoOrderByUpdatedAtAsc(Long sellerNo);

    List<ChatRoomResponseDto> findByBuyerNoOrderByUpdatedAtAsc(Long buyerNo);

    List<ChatRoomResponseDto> findBySaleNoOrderByUpdatedAtAsc(Long saleNo);

    List<ChatRoomResponseDto> findBySaleNoAndBuyerNoOrderByUpdatedAtAsc(Long saleNo, Long buyerNo);
}