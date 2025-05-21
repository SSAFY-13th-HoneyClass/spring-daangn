package com.ssafy.daangn_demo.service;

import com.ssafy.daangn_demo.entity.UserEntity;
import com.ssafy.daangn_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void create(UserEntity user) {
        userRepository.save(user);
    }

    public UserEntity getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
