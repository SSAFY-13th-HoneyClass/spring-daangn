package com.ssafy.springdaangn.repository;

import com.ssafy.springdaangn.domain.Like;
import com.ssafy.springdaangn.domain.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeId> {
    Optional<Like> findByUser_UserIdAndPost_PostId(Long userId, Long postId);
    boolean existsByUser_UserIdAndPost_PostId(Long userId, Long postId);
}

