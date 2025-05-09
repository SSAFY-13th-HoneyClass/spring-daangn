package com.ssafy.springdaangn.Repository;

import com.ssafy.springdaangn.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 예: Optional<User> findById(String id);
}
