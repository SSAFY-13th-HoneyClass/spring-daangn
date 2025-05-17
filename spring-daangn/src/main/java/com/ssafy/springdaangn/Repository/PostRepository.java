package com.ssafy.springdaangn.Repository;

import com.ssafy.springdaangn.Domain.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findBySellerUserId(Long userId);

    //List<Post> findByNeighborhood(Long neighborhoodId);

    @EntityGraph(attributePaths = {"seller"})
    @Query("select p from Post p")
    List<Post> findAllWithSeller();
}