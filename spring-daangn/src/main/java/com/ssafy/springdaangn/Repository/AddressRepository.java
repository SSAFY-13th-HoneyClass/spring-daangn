package com.ssafy.springdaangn.Repository;

import com.ssafy.springdaangn.Domain.Address;
import com.ssafy.springdaangn.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByUser(User user);
}
