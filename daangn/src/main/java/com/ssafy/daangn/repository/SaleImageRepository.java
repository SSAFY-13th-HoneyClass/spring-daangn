package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.SaleImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleImageRepository extends JpaRepository<SaleImage, Long> {
    List<SaleImage> findBySaleNo(Long no);
    void deleteBySaleNo(Long no);

}

