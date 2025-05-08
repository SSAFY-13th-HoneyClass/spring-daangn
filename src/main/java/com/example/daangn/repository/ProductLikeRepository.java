package com.example.daangn.repository;

import com.example.daangn.domain.Product;
import com.example.daangn.domain.ProductLike;
import com.example.daangn.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    List<ProductLike> findByUser(User user);
    List<ProductLike> findByProduct(Product product);
    Optional<ProductLike> findByProductAndUser(Product product, User user);
    void deleteByProductAndUser(Product product, User user);
    long countByProduct(Product product);
}
