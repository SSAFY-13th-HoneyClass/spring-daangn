package com.ssafy.daangn_demo.repository;

import com.ssafy.daangn_demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
