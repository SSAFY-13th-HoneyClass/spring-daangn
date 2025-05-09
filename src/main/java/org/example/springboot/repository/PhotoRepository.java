package org.example.springboot.repository;

import org.example.springboot.domain.Photo;
import org.example.springboot.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByPost(Post post);
}