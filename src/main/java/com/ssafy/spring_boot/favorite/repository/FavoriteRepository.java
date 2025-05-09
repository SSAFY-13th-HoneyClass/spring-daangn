package com.ssafy.spring_boot.favorite.repository;

import com.ssafy.spring_boot.favorite.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite,Integer> {
    List<Favorite> findAllByUserId(Long userId);
}
