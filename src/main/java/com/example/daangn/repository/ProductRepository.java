package com.example.daangn.repository;

import com.example.daangn.domain.Location;
import com.example.daangn.domain.Product;
import com.example.daangn.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByIsSell(Boolean isSell, Pageable pageable);
    Page<Product> findByUser(User user, Pageable pageable);
    Page<Product> findByLocation(Location location, Pageable pageable);
    Page<Product> findByCategory(String category, Pageable pageable);
    Page<Product> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword, Pageable pageable);
    List<Product> findTop10ByOrderByViewsDesc();
}
