package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    Optional<Sale> findByNo(Long no);

}

