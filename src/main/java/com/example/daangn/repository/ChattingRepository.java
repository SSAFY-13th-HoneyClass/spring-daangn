package com.example.daangn.repository;


import com.example.daangn.domain.Chatting;
import com.example.daangn.domain.Product;
import com.example.daangn.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChattingRepository extends JpaRepository<Chatting, Long> {
    List<Chatting> findBySellUser(User sellUser);
    List<Chatting> findByBuyUser(User buyUser);
    List<Chatting> findByProduct(Product product);
    Optional<Chatting> findByProductAndSellUserAndBuyUser(Product product, User sellUser, User buyUser);
}
