package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // ① 판매자 기준
    List<ChatRoom> findBySellerNoOrderByNoAsc(Long sellerNo);

    // ② 구매자 기준
    List<ChatRoom> findByBuyerNoOrderByNoAsc(Long buyerNo);

    // ③ 상품 기준
    List<ChatRoom> findBySaleNoOrderByNoAsc(Long saleNo);

    // ④ 상품 + 구매자 기준
    List<ChatRoom> findBySaleNoAndBuyerNoOrderByNoAsc(Long saleNo, Long buyerNo);
}

