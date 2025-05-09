// User Repository
package org.example.springboot.repository;

import org.example.springboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUserId(Long userId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}