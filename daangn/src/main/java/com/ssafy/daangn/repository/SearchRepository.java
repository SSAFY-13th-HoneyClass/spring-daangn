package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.Search;
import com.ssafy.daangn.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {
    List<Search> findByUser(User user);
    List<Search> findByKeywordContaining(String keyword);
}
