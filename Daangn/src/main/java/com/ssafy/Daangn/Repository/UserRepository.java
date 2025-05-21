package com.ssafy.Daangn.Repository;

import com.ssafy.Daangn.Domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // 사용자와 위치 정보 함께 조회
    @EntityGraph(attributePaths = {"location"})
    List<User> findAll();


    // 위치별 사용자 조회
    @EntityGraph(attributePaths = {"location"})
    List<User> findByLocationLocationId(Integer locationId);

    // 닉네임으로 찾기
    User findByNickname(String nickname);

    Optional<User> findByUserId(String userId);
}
