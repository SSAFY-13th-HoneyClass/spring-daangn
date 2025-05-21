package com.ssafy.spring_boot.user.service;

import com.ssafy.spring_boot.chat.dto.ChatRoomDTO;
import com.ssafy.spring_boot.product.dto.ProductDTO;
import com.ssafy.spring_boot.user.dto.LoginInfoDTO;
import com.ssafy.spring_boot.user.dto.UserDTO;

import java.util.List;

/**
 * 사용자 관련 비즈니스 로직을 정의하는 인터페이스
 */
public interface UserService {

    /**
     * 이메일과 비밀번호로 로그인 처리
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @return 로그인 정보가 담긴 DTO
     */
    LoginInfoDTO login(String email, String password);

    /**
     * 사용자 ID로 사용자 상세 정보 조회
     * @param userId 사용자 ID
     * @return 사용자 정보가 담긴 DTO
     */
    UserDTO getUserDetail(Long userId);

    /**
     * 특정 사용자가 등록한 중고상품 리스트 조회
     * @param userId 사용자 ID
     * @return 해당 사용자가 등록한 상품 목록
     */
    List<ProductDTO> getUserProducts(Long userId);

    /**
     * 특정 사용자가 참여 중인 채팅방 목록 조회
     * @param userId 사용자 ID
     * @return 해당 사용자가 참여 중인 채팅방 목록
     */
    List<ChatRoomDTO> getUserChatRooms(Long userId);

    /**
     * 특정 사용자가 관심 있음 표시한 상품 ID 목록 조회
     * @param userId 사용자 ID
     * @return 관심 상품 ID 목록
     */
    List<Long> getFavoriteProductIds(Long userId);
}