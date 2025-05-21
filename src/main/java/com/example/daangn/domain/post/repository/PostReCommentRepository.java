package com.example.daangn.domain.post.repository;

import com.example.daangn.domain.post.entity.Post;
import com.example.daangn.domain.post.entity.PostReComment;
import com.example.daangn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostReCommentRepository extends JpaRepository<PostReComment, Long> {
    List<PostReComment> findByPost(Post post);
    List<PostReComment> findByUser(User user);
    long countByPost(Post post);
}
