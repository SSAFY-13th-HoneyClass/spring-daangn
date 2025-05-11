package com.example.daangn.repository;


import com.example.daangn.domain.PostComment;
import com.example.daangn.domain.PostCommentLike;
import com.example.daangn.domain.PostReComment;
import com.example.daangn.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostCommentLikeRepository extends JpaRepository<PostCommentLike, Long> {
    Optional<PostCommentLike> findByCommentAndUser(PostComment comment, User user);
    Optional<PostCommentLike> findByRecommentAndUser(PostReComment recomment, User user);
    void deleteByCommentAndUser(PostComment comment, User user);
    void deleteByRecommentAndUser(PostReComment recomment, User user);
}
