package com.ssafy.daangn.service;

import com.ssafy.daangn.domain.User;
import com.ssafy.daangn.dto.UserResponseDto;
import com.ssafy.daangn.dto.UserUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface UserService {
    // 기본 키(PK)로 조회 (상세 보기용)
    Optional<User> findByNo(Long no);

    // 아이디와 이메일로 사용자 정보 찾기 (비밀번호 변경 등)
    Optional<User> findByIdAndEmail(String id, String email);

    // 아이디로 사용자 존재 여부 확인(아이디 중복 체크)
    boolean existsById(String id);

    // 닉네임으로 사용자 존재 여부 확인(닉네임 중복 체크)
    boolean existsByNickname(String nickname);

    // 아이디 비밀번호로 로그인(로그인)
    Optional<User> findByIdAndPassword(String id, String password);

    User save(User user);

    UserResponseDto update(Long no, UserUpdateRequest request);

    boolean delete(String id);


}
