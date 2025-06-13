package com.ssafy.springdaangn.service;

import com.ssafy.springdaangn.domain.User;
import com.ssafy.springdaangn.exception.UserNotFoundException;
import com.ssafy.springdaangn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        // JWT 토큰으로 인증된 사용자인지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증 정보가 없어!");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            throw new IllegalArgumentException("잘못된 인증 정보");
        }

        UserDetails userDetails = (UserDetails) principal;
        String authenticatedUserId = userDetails.getUsername();

        // 인증된 사용자의 실제 User 엔티티 조회
        User authenticatedUser = userRepository.findByid(authenticatedUserId)
                .orElseThrow(() -> new UserNotFoundException("인증된 사용자를 찾을 수 없습니다."));

        // 요청한 userId와 인증된 사용자의 userId가 일치하는지 확인
        if (!authenticatedUser.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 정보만 조회할 수 있습니다.");
        }

        return authenticatedUser;
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