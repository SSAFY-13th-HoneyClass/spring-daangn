package com.ssafy.daangn.service;

import com.ssafy.daangn.domain.ChatRoom;
import com.ssafy.daangn.domain.Sale;
import com.ssafy.daangn.domain.User;
import com.ssafy.daangn.dto.ChatRoomDto;
import com.ssafy.daangn.repository.ChatMessageRepository;
import com.ssafy.daangn.repository.ChatRoomRepository;
import com.ssafy.daangn.repository.SaleRepository;
import com.ssafy.daangn.repository.UserRepository;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SaleRepository saleRepository;
    private final UserRepository userRepository;

    @Override
    public ChatRoom save(ChatRoomDto chatRoom) {
        Sale sale = saleRepository.findByNo(chatRoom.getSaleNo()).orElseThrow(() -> new EntityNotFoundException("Sale not found"));
        User seller = userRepository.findByNo(chatRoom.getSellerNo()).orElseThrow(() -> new EntityNotFoundException("seller not found"));
        User buyer = userRepository.findByNo(chatRoom.getBuyerNo()).orElseThrow(() -> new EntityNotFoundException("buyer not found"));

        ChatRoom chatRoomEntity = ChatRoom.builder().sale(sale).seller(seller).buyer(buyer).build();
        return chatRoomRepository.save(chatRoomEntity);
    }

    @Override
    public void delete(ChatRoomDto chatRoom) {
        chatMessageRepository.deleteByChatRoomNo(chatRoom.getNo());

        Sale sale = saleRepository.findByNo(chatRoom.getSaleNo()).orElseThrow(() -> new EntityNotFoundException("Sale not found"));
        User seller = userRepository.findByNo(chatRoom.getSellerNo()).orElseThrow(() -> new EntityNotFoundException("seller not found"));
        User buyer = userRepository.findByNo(chatRoom.getBuyerNo()).orElseThrow(() -> new EntityNotFoundException("buyer not found"));

        ChatRoom chatRoomEntity = ChatRoom.builder().sale(sale).seller(seller).buyer(buyer).build();
        chatRoomRepository.delete(chatRoomEntity);
    }

    @Override
    public List<ChatRoom> findBySellerNoOrderByUpdatedAtAsc(Long sellerNo) {
        return chatRoomRepository.findBySellerNoOrderByUpdatedAtAsc(sellerNo);
    }

    @Override
    public List<ChatRoom> findByBuyerNoOrderByUpdatedAtAsc(Long buyerNo) {
        return chatRoomRepository.findByBuyerNoOrderByUpdatedAtAsc(buyerNo);
    }

    @Override
    public List<ChatRoom> findBySaleNoOrderByUpdatedAtAsc(Long saleNo) {
        return chatRoomRepository.findBySaleNoOrderByUpdatedAtAsc(saleNo);
    }

    @Override
    public List<ChatRoom> findBySaleNoAndBuyerNoOrderByUpdatedAtAsc(Long saleNo, Long buyerNo) {
        return chatRoomRepository.findBySaleNoAndBuyerNoOrderByUpdatedAtAsc(saleNo, buyerNo);
    }
}
