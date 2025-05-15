package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.SaleLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleLikeRepository extends JpaRepository<SaleLike, Long> {
    void deleteBySaleNo(Long saleNo);
}

