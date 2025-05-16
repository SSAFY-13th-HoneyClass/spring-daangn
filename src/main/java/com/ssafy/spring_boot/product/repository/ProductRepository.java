package com.ssafy.spring_boot.product.repository;

import com.ssafy.spring_boot.product.domain.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository  extends JpaRepository<Product,Long> {
    List<Product> findAllByCategory_Id(Integer categoryId);
    List<Product> findAllBySeller_Id(Long sellerId);
    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.seller " +
            "JOIN FETCH p.category " +
            "JOIN FETCH p.region " +
            "WHERE p.id = :productId")
    Optional<Product> findByIdWithAll(@Param("productId") Long productId);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :productId")
    Optional<Product> findByIdWithCategory(@Param("productId") Long productId);

    // src/main/java/com/ssafy/spring_boot/product/repository/ProductRepository.java에 추가
    @Query("SELECT DISTINCT p FROM Product p " +
            "JOIN FETCH p.seller " +
            "JOIN FETCH p.category " +
            "JOIN FETCH p.region")
    List<Product> findAllWithDetails();

    // 상품 ID와 연관된 이미지를 함께 조회 (BatchSize 활용)
    @EntityGraph(attributePaths = {"seller", "category", "region"})
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByIdWithDetails(@Param("productId") Long productId);
}
