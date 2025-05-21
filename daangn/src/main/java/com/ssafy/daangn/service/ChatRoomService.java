package com.ssafy.daangn.service;

import com.ssafy.daangn.domain.ChatRoom;
import com.ssafy.daangn.dto.ChatRoomDto;

import java.util.List;

public interface ChatRoomService {

    ChatRoom save(ChatRoomDto chatRoom);

//    ChatRoom update(ChatRoom chatRoom);

    void delete(ChatRoomDto chatRoom);


        // ① 판매자 기준
    List<ChatRoom> findBySellerNoOrderByUpdatedAtAsc(Long sellerNo);

    // ② 구매자 기준
    List<ChatRoom> findByBuyerNoOrderByUpdatedAtAsc(Long buyerNo);

    // ③ 상품 기준
    List<ChatRoom> findBySaleNoOrderByUpdatedAtAsc(Long saleNo);

    // ④ 상품 + 구매자 기준
    List<ChatRoom> findBySaleNoAndBuyerNoOrderByUpdatedAtAsc(Long saleNo, Long buyerNo);

}
