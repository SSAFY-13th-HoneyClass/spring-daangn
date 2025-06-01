// SaleStatusRepository.java
package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleStatusRepository extends JpaRepository<SaleStatus, String> { // String으로 변경
}