package com.ssafy.daangn_demo.repository;

import com.ssafy.daangn_demo.entity.ProductEntity;
import com.ssafy.daangn_demo.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByWriterId(Long writerId);

    @EntityGraph(attributePaths = "writer")
    List<ProductEntity> findAll();
}
