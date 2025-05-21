package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    Optional<Sale> findByNo(Long no);

    //판매자 pk 기준 + 생성일 정렬 (최신순)
    List<Sale> findByUser_IdOrderByCreatedAtDesc(String userId);

    List<Sale> findByTitleContainingOrContentContainingOrderByCreatedAtDesc(String titleKeyword, String contentKeyword);


    @Query(value = """
                SELECT * FROM sales s
                WHERE (
                  6371 * acos(
                    cos(radians(:lat)) * cos(radians(s.latitude)) *
                    cos(radians(s.longitude) - radians(:lng)) +
                    sin(radians(:lat)) * sin(radians(s.latitude))
                  )
                ) <= :distance
                ORDER BY s.created_at DESC
                LIMIT 100
            """, nativeQuery = true)
    List<Sale> findByLocationNear(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("distance") double maxDistanceKm
    );

    Sale findByNo(long no);

    @Query("""
      SELECT DISTINCT s FROM Sale s
      JOIN FETCH s.user
      JOIN FETCH s.category
      JOIN FETCH s.status
    """)
    List<Sale> findAllWithUserCategoryStatus();


    List<Sale> findAll();

}

