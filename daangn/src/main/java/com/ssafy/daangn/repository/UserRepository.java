package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

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

    Optional<User> findById(String id);


}

