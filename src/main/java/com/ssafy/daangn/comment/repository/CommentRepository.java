package com.ssafy.daangn.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.daangn.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글 ID 기준으로 전체 댓글 (삭제되지 않은 것만)
    List<Comment> findByBoard_BoardIdAndIsDeletedFalse(Long boardId);

    // 부모 댓글 ID 기준 대댓글 (삭제되지 않은 것만)
    List<Comment> findByParent_CommentIdAndIsDeletedFalse(Long parentCommentId);

    // 게시글 내에서 부모 없는 댓글만 조회 (최상위 댓글)
    List<Comment> findByBoard_BoardIdAndParentIsNullAndIsDeletedFalse(Long boardId);
}
