package com.ssafy.Daangn.Repository;

import com.ssafy.Daangn.Domain.Board;
import com.ssafy.Daangn.Domain.DmRoom;
import com.ssafy.Daangn.Domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DmRoomRepository extends JpaRepository<DmRoom, Integer> {
    // 채팅방 조회 시 사용자와 게시글 정보 함께 로드
    @EntityGraph(attributePaths = {"user", "board"})
    List<DmRoom> findAll();

    @EntityGraph(attributePaths = {"board", "board.seller"})
    List<DmRoom> findByUser(User user);

    // 특정 사용자와 게시글 간의 채팅방 찾기
    @EntityGraph(attributePaths = {"user", "board"})
    Optional<DmRoom> findByUserAndBoard(User user, Board board);
}