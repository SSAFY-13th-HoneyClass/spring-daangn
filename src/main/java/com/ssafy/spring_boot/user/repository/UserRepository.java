package com.ssafy.spring_boot.user.repository;

import com.ssafy.spring_boot.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email); // 이메일 존재하니?
    Optional<User> findByEmail(String email); // 이메일로 사용자 찾기
}
