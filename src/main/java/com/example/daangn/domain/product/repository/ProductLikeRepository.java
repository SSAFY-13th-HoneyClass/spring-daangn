package com.example.daangn.domain.product.repository;

import com.example.daangn.domain.product.entity.Product;
import com.example.daangn.domain.product.entity.ProductLike;
import com.example.daangn.domain.user.entity.User;
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
