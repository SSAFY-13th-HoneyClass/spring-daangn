package com.ssafy.daangn.boardphoto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.daangn.boardphoto.entity.BoardPhoto;

public interface BoardPhotoRepository extends JpaRepository<BoardPhoto, Long> {
    List<BoardPhoto> findByBoard_BoardIdOrderByPhotoOrder(Long boardId);
}
