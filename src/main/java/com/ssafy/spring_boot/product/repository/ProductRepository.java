package com.ssafy.spring_boot.product.repository;

import com.ssafy.spring_boot.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository  extends JpaRepository<Product,Long> {
    List<Product> findAllByCategory_Id(Integer categoryId);
    List<Product> findAllBySeller_Id(Long sellerId);
}
