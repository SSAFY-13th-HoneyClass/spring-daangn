package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    // 이메일로 사용자 조회
    Optional<Users> findByEmail(String email);

    // 아이디로 사용자 조회
    Optional<Users> findById(String id);

    // 닉네임으로 사용자 존재 여부 확인
    boolean existsByNickname(String nickname);
}
