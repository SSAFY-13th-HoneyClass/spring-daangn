package com.ssafy.Daangn.Repository;

import com.ssafy.Daangn.Domain.Board;
import com.ssafy.Daangn.Domain.Like;
import com.ssafy.Daangn.Domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    @EntityGraph(attributePaths = {"board", "board.seller"})
    List<Like> findByUser(User user);

    // 사용자가 좋아요한 게시글 목록
    @Query("SELECT l.board FROM Like l WHERE l.user = :user AND l.isLike = true")
    List<Board> findLikedBoardsByUser(@Param("user") User user);

}