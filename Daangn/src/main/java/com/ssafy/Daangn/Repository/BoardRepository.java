package com.ssafy.Daangn.Repository;

import com.ssafy.Daangn.Domain.Board;
import com.ssafy.Daangn.Domain.Location;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    // 기본 조회 - seller, location 함께 로드
//    @EntityGraph(attributePaths = {"seller", "location"})
    List<Board> findAll();

    // 지역 기준 검색에서 seller 함께 로드
    @EntityGraph(attributePaths = {"seller"})
    List<Board> findByLocation(Location location);

    // 카테고리별 검색
    @EntityGraph(attributePaths = {"seller", "location"})
    List<Board> findByCategory(String category);

    // 제목 키워드 검색
    @EntityGraph(attributePaths = {"seller", "location"})
    List<Board> findByTitleContaining(String keyword);

    // 판매 여부로 검색
    @EntityGraph(attributePaths = {"seller", "location"})
    List<Board> findByIsSell(boolean isSell);


    // EntityGraph를 사용하여 seller와 location을 함께 로드
    @EntityGraph(attributePaths = {"seller", "location"})
    @Query("SELECT b FROM Board b")
    List<Board> findAllWithSellerAndLocation();

    Optional<Board> findBytitle(String title);
}