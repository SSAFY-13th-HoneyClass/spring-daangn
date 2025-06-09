package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 기존 메서드들 (Lazy Loading 문제 있음)
    List<ChatRoom> findBySellerNoOrderByUpdatedAtAsc(Long sellerNo);
    List<ChatRoom> findByBuyerNoOrderByUpdatedAtAsc(Long buyerNo);
    List<ChatRoom> findBySaleNoOrderByUpdatedAtAsc(Long saleNo);
    List<ChatRoom> findBySaleNoAndBuyerNoOrderByUpdatedAtAsc(Long saleNo, Long buyerNo);
    void deleteBySaleNo(Long saleNo);
    List<ChatRoom> findBySaleNo(Long sellerNo);

    // ✅ 새로 추가: JOIN FETCH로 모든 연관 엔티티를 미리 로딩
    @Query("SELECT cr FROM ChatRoom cr " +
            "JOIN FETCH cr.sale s " +
            "JOIN FETCH cr.seller seller " +
            "JOIN FETCH cr.buyer buyer " +
            "WHERE seller.no = :sellerNo " +
            "ORDER BY cr.updatedAt ASC")
    List<ChatRoom> findBySellerNoWithAllEntities(@Param("sellerNo") Long sellerNo);

    @Query("SELECT cr FROM ChatRoom cr " +
            "JOIN FETCH cr.sale s " +
            "JOIN FETCH cr.seller seller " +
            "JOIN FETCH cr.buyer buyer " +
            "WHERE buyer.no = :buyerNo " +
            "ORDER BY cr.updatedAt ASC")
    List<ChatRoom> findByBuyerNoWithAllEntities(@Param("buyerNo") Long buyerNo);

    @Query("SELECT cr FROM ChatRoom cr " +
            "JOIN FETCH cr.sale s " +
            "JOIN FETCH cr.seller seller " +
            "JOIN FETCH cr.buyer buyer " +
            "WHERE s.no = :saleNo " +
            "ORDER BY cr.updatedAt ASC")
    List<ChatRoom> findBySaleNoWithAllEntities(@Param("saleNo") Long saleNo);

    @Query("SELECT cr FROM ChatRoom cr " +
            "JOIN FETCH cr.sale s " +
            "JOIN FETCH cr.seller seller " +
            "JOIN FETCH cr.buyer buyer " +
            "WHERE s.no = :saleNo AND buyer.no = :buyerNo " +
            "ORDER BY cr.updatedAt ASC")
    List<ChatRoom> findBySaleNoAndBuyerNoWithAllEntities(@Param("saleNo") Long saleNo, @Param("buyerNo") Long buyerNo);
}