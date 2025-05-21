package com.example.daangn.domain.user.repository;


import com.example.daangn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(String id);
    boolean existsById(String id);
    Optional<User> findByPhone(String phone);
    Optional<User> findByUuid(Long uuid);
}