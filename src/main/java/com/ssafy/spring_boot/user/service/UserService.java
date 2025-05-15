package com.ssafy.spring_boot.user.service;

import com.ssafy.spring_boot.chat.dto.ChatRoomDTO;
import com.ssafy.spring_boot.product.dto.ProductDTO;
import com.ssafy.spring_boot.user.dto.LoginInfoDTO;
import com.ssafy.spring_boot.user.dto.UserDTO;

import java.util.List;

public interface UserService {
    // 이메일이랑 비밀번호로 로그인
    // LoginInfoDTO 필요
    LoginInfoDTO login(String email, String password);

    // UUID로 유저 정보 가져오기
    // UserDTO 필요
    UserDTO getUserDetail(Long userId);

    // 사용자가 등록한 중고상품 리스트 조회
    // ProductDTO 필요
    List<ProductDTO> getUserProducts(Long userId);

    //사용자가 참여중인 채팅방 목록 조회
    List<ChatRoomDTO> getUserChatRooms(Long userId);

    //사용자가 관심있음 누른 중고상품 조회
    List<Long> getFavoriteProductIds(Long userId);

    // 사용자 매너온도 계산
//    double calculateUserManner(Long userId);
}