package com.ssafy.daangn.favorite.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.favorite.entity.Favorite;
import com.ssafy.daangn.member.entity.Member;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByMemberAndBoard(Member member, Board board);
    List<Favorite> findByBoard(Board board);
    void deleteByMemberAndBoard(Member member, Board board);
}
