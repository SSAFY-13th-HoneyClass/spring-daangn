package com.ssafy.daangn.service;

import com.ssafy.daangn.domain.ChatRoom;
import com.ssafy.daangn.domain.Sale;
import com.ssafy.daangn.domain.User;
import com.ssafy.daangn.dto.ChatRoomDto;
import com.ssafy.daangn.dto.ChatRoomResponseDto;
import com.ssafy.daangn.repository.ChatMessageRepository;
import com.ssafy.daangn.repository.ChatRoomRepository;
import com.ssafy.daangn.repository.SaleRepository;
import com.ssafy.daangn.repository.UserRepository;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SaleRepository saleRepository;
    private final UserRepository userRepository;

    @Override
    public ChatRoomResponseDto save(ChatRoomDto chatRoom) {
        Sale sale = saleRepository.findByNo(chatRoom.getSaleNo()).orElseThrow(() -> new EntityNotFoundException("Sale not found"));
        User seller = userRepository.findByNo(chatRoom.getSellerNo()).orElseThrow(() -> new EntityNotFoundException("seller not found"));
        User buyer = userRepository.findByNo(chatRoom.getBuyerNo()).orElseThrow(() -> new EntityNotFoundException("buyer not found"));

        ChatRoom chatRoomEntity = ChatRoom.builder().sale(sale).seller(seller).buyer(buyer).build();
        return new ChatRoomResponseDto(chatRoomRepository.save(chatRoomEntity));
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
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> findBySellerNoOrderByUpdatedAtAsc(Long sellerNo) {
        List<ChatRoom> chatRooms = chatRoomRepository.findBySellerNoWithAllEntities(sellerNo);
        return chatRooms.stream()
                .map(ChatRoomResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> findByBuyerNoOrderByUpdatedAtAsc(Long buyerNo) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByBuyerNoWithAllEntities(buyerNo);
        return chatRooms.stream()
                .map(ChatRoomResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> findBySaleNoOrderByUpdatedAtAsc(Long saleNo) {
        List<ChatRoom> chatRooms = chatRoomRepository.findBySaleNoWithAllEntities(saleNo);
        return chatRooms.stream()
                .map(ChatRoomResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> findBySaleNoAndBuyerNoOrderByUpdatedAtAsc(Long saleNo, Long buyerNo) {
        List<ChatRoom> chatRooms = chatRoomRepository.findBySaleNoAndBuyerNoWithAllEntities(saleNo, buyerNo);
        return chatRooms.stream()
                .map(ChatRoomResponseDto::new)
                .collect(Collectors.toList());
    }
}