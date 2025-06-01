package com.ssafy.daangn.favorite.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.favorite.entity.Favorite;
import com.ssafy.daangn.favorite.entity.FavoriteId;
import com.ssafy.daangn.member.entity.Member;

// 좋아요(Favorite) 관련 데이터베이스 연산을 처리하는 JPA 리포지토리
// 복합키(FavoriteId)를 기본 키로 사용
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {

    // 특정 회원이 특정 게시글을 좋아요했는지 여부 조회
    Optional<Favorite> findByMemberAndBoard(Member member, Board board);

    // 특정 게시글을 좋아요한 전체 Favorite 목록 조회
    List<Favorite> findByBoard(Board board);

    // 특정 회원이 좋아요한 모든 Favorite 목록 조회
    List<Favorite> findByMember(Member member);

    // 특정 회원이 특정 게시글에 누른 좋아요를 삭제
    void deleteByMemberAndBoard(Member member, Board board);
}
