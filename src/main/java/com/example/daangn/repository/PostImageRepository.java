package com.example.daangn.repository;


import com.example.daangn.domain.Post;
import com.example.daangn.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPost(Post post);
    void deleteByPost(Post post);
}
