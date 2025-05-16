package com.example.daangn.domain.chat.repository;


import com.example.daangn.domain.chat.entity.Chatting;
import com.example.daangn.domain.product.entity.Product;
import com.example.daangn.domain.user.entity.User;
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
