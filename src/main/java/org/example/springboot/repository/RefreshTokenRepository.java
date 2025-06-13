package org.example.springboot.repository;

import org.example.springboot.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByUserId(Long userId);
    
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    
    void deleteByUserId(Long userId);
    
    void deleteByRefreshToken(String refreshToken);
} 