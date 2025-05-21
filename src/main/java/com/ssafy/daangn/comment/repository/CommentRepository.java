package com.ssafy.daangn.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ssafy.daangn.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글 ID 기준으로 전체 댓글 (삭제되지 않은 것만)
    List<Comment> findByBoard_BoardIdAndIsDeletedFalse(Long boardId);

    // 부모 댓글 ID 기준 대댓글 (삭제되지 않은 것만)
    List<Comment> findByParent_CommentIdAndIsDeletedFalse(Long parentCommentId);

    // 게시글 내에서 부모 없는 댓글만 조회 (최상위 댓글)

    // 기존 메서드
    List<Comment> findByBoard_BoardIdAndParentIsNullAndIsDeletedFalse(Long boardId);
    
    // N+1 해결용 fetch join 메서드
    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.member " +
           "JOIN FETCH c.board " +
           "WHERE c.board.boardId = :boardId AND c.isDeleted = false")
    List<Comment> findWithMemberAndBoardByBoardId(@Param("boardId") Long boardId);
}
