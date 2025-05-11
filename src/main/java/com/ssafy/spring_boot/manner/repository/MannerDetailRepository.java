package com.ssafy.spring_boot.manner.repository;

import com.ssafy.spring_boot.manner.domain.MannerDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MannerDetailRepository extends JpaRepository<MannerDetail,Long> {
}
