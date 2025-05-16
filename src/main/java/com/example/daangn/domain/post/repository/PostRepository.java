package com.example.daangn.domain.post.repository;


import com.example.daangn.domain.post.entity.Post;
import com.example.daangn.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUser(User user, Pageable pageable);
    Page<Post> findByHot(Boolean hot, Pageable pageable);
    Page<Post> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword, Pageable pageable);
    Page<Post> findByPostTagContaining(String tag, Pageable pageable);
    List<Post> findTop10ByOrderByViewsDesc();
    @Query("select p from Post p left join fetch p.user")
    List<Post> findPostbyFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"user"})
    List<Post> findAll();
}
