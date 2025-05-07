package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.Search;
import com.ssafy.daangn.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchRepository extends JpaRepository<Search, Long> {
    List<Search> findByUser(User user);
    List<Search> findByKeywordContaining(String keyword);
}
