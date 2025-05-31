package com.ssafy.daangn.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.daangn.board.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 삭제되지 않은 게시글 전체 조회
    List<Board> findByIsDeletedFalse();

    // 삭제된 게시글만 조회
    List<Board> findByIsDeletedTrue();

    // 특정 회원의 게시글 조회
    List<Board> findByMember_MemberIdAndIsDeletedFalse(Long memberId);

    // 제목 키워드 검색
    List<Board> findByTitleContainingAndIsDeletedFalse(String keyword);
}
