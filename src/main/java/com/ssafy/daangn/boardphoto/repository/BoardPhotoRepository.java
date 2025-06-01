package com.ssafy.daangn.boardphoto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.daangn.boardphoto.entity.BoardPhoto;

public interface BoardPhotoRepository extends JpaRepository<BoardPhoto, Long> {

    // 특정 게시글 ID에 해당하는 모든 사진을 생성 시각으로 조회
    List<BoardPhoto> findByBoard_BoardIdOrderByCreatedAtAsc(Long boardId);
}
