package com.ssafy.jpatest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);
}

interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findBySeller(User seller);
    List<Board> findByPriceGreaterThanEqual(int price);
}

