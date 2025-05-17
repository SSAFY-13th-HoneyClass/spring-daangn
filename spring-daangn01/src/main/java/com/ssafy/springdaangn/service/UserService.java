package com.ssafy.springdaangn.service;

import com.ssafy.springdaangn.Domain.User;
import com.ssafy.springdaangn.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long userId, User updated) {
        User user = getUser(userId);
        user.setId(updated.getId());
        user.setPassword(updated.getPassword());
        user.setNickname(updated.getNickname());
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = getUser(userId);
        userRepository.delete(user);
    }
}
