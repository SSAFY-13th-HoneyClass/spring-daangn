package com.ssafy.Daangn.Repository;

import com.ssafy.Daangn.Domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    // 지역명으로 검색
    List<Location> findByLocationName(String locationName);

}