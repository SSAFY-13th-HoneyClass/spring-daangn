package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.domain.User;
import org.example.springboot.dto.UserDto;
import org.example.springboot.exception.ItemNotFoundException;
import org.example.springboot.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public UserDto.UserResponse signUp(UserDto.SignUpRequest request) {
        log.debug("Attempting to sign up user with email: {}", request.getEmail());
        
        // 중복 이메일 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + request.getEmail());
        }

        // 중복 닉네임 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다: " + request.getNickname());
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 유저 엔티티 생성 및 저장
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .profile(request.getProfile())
                .profileImgPath(request.getProfileImgPath() != null ? 
                    request.getProfileImgPath() : "/images/default.jpg")
                .role("USER")
                .build();

        User savedUser = userRepository.save(user);
        log.debug("User signed up successfully with id: {}", savedUser.getUserId());
        
        return UserDto.UserResponse.from(savedUser);
    }

    /**
     * 로그인
     */
    public UserDto.LoginResponse login(UserDto.LoginRequest request) {
        log.debug("Attempting to login user with email: {}", request.getEmail());
        
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다: " + request.getEmail());
        }

        // 암호화된 비밀번호 비교
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        log.debug("User logged in successfully: {}", user.getEmail());
        
        return UserDto.LoginResponse.of("로그인 성공", user);
    }

    /**
     * 사용자 프로필 조회
     */
    public UserDto.UserResponse getUserProfile(Long userId) {
        log.debug("Retrieving user profile for id: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ItemNotFoundException("User not found with id: " + userId));
        
        return UserDto.UserResponse.from(user);
    }

    /**
     * 모든 사용자 목록 조회
     */
    public List<UserDto.UserListResponse> getAllUsers() {
        log.debug("Retrieving all users");
        
        List<User> users = userRepository.findAll();
        
        return users.stream()
                .map(UserDto.UserListResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 이메일로 사용자 조회
     */
    public UserDto.UserResponse getUserByEmail(String email) {
        log.debug("Retrieving user by email: {}", email);
        
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ItemNotFoundException("User not found with email: " + email);
        }
        
        return UserDto.UserResponse.from(user);
    }

    /**
     * 사용자 삭제
     */
    @Transactional
    public void deleteUser(Long userId) {
        log.debug("Deleting user with id: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ItemNotFoundException("User not found with id: " + userId));
        
        userRepository.delete(user);
        log.debug("User deleted successfully with id: {}", userId);
    }
}