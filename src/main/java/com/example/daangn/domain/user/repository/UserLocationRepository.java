package com.example.daangn.domain.user.repository;


import com.example.daangn.domain.user.entity.UserLocation;
import com.example.daangn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
    List<UserLocation> findByUser(User user);
    Optional<UserLocation> findByUserAndRep(User user, Boolean rep);
}
