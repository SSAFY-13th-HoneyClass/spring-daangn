package com.example.daangn.domain.product.repository;

import com.example.daangn.domain.product.entity.Product;
import com.example.daangn.domain.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProduct(Product product);
    void deleteByProduct(Product product);
}
