package com.ssafy.daangn_demo.repository;

import com.ssafy.daangn_demo.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}
