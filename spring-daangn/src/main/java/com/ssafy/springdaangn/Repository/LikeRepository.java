package com.ssafy.springdaangn.Repository;

import com.ssafy.springdaangn.Domain.Like;
import com.ssafy.springdaangn.Domain.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeId> {
    // 추가적인 쿼리 메서드 정의 가능
}
