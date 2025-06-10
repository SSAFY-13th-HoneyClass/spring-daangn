package com.ssafy.springdaangn.service;

import com.ssafy.springdaangn.Domain.User;
import com.ssafy.springdaangn.Exception.UserNotFoundException;
import com.ssafy.springdaangn.Repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long userId, User updated) {
        User user = getUser(userId); // 이미 UserNotFoundException 처리됨
        user.setId(updated.getId());
        user.setPassword(updated.getPassword());
        user.setNickname(updated.getNickname());
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = getUser(userId); // 이미 UserNotFoundException 처리됨
        userRepository.delete(user);
    }
}