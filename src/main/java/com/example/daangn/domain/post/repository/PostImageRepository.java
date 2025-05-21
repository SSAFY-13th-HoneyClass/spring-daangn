package com.example.daangn.domain.post.repository;


import com.example.daangn.domain.post.entity.Post;
import com.example.daangn.domain.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPost(Post post);
    void deleteByPost(Post post);
}
