package com.ssafy.springdaangn.repository;

import com.ssafy.springdaangn.domain.Address;
import com.ssafy.springdaangn.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByUser(User user);

    Address findByUser_UserId(Long userId);

    Address deleteByUser(User user);
}
