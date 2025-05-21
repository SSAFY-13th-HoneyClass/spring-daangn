package com.example.daangn.domain.post.repository;


import com.example.daangn.domain.post.entity.PostComment;
import com.example.daangn.domain.post.entity.PostCommentLike;
import com.example.daangn.domain.post.entity.PostReComment;
import com.example.daangn.domain.user.entity.User;
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
