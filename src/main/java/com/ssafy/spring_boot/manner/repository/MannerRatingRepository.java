package com.ssafy.spring_boot.manner.repository;

import com.ssafy.spring_boot.manner.domain.MannerDetail;
import com.ssafy.spring_boot.manner.domain.MannerRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MannerRatingRepository extends JpaRepository<MannerRating, Long> {
    List<MannerRating> findAllByRatedUserId(Long userId); // ✅ OK
}

