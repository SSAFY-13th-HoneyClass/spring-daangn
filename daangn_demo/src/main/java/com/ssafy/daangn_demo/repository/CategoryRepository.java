package com.ssafy.daangn_demo.repository;

import com.ssafy.daangn_demo.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
