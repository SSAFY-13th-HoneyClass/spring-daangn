package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}