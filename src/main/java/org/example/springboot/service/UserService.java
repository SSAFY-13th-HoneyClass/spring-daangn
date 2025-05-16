package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import org.example.springboot.domain.User;
import org.example.springboot.dto.UserDto;
import org.example.springboot.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 1. 회원가입 - 비밀번호 암호화 적용
     */
    @Transactional
    public Long registerUser(UserDto.RegisterRequest requestDto) {
        // 중복 이메일 체크
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 중복 닉네임 체크
        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 유저 엔티티 생성 및 저장
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword) // 암호화된 비밀번호 저장
                .phone(requestDto.getPhone())
                .name(requestDto.getName())
                .profile(requestDto.getProfile())
                .nickname(requestDto.getNickname())
                .profileImgPath(requestDto.getProfileImgPath())
                .role("USER")
                .build();

        User savedUser = userRepository.save(user);
        return savedUser.getUserId();
    }

    /**
     * 2. 로그인 - 암호화된 비밀번호 비교
     */
    @Transactional(readOnly = true)
    public UserDto.LoginResponse login(UserDto.LoginRequest requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }

        // 암호화된 비밀번호 비교
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 실제 프로젝트에서는 JWT 토큰 생성 로직 필요
        String token = "sample-token-" + System.currentTimeMillis();

        return UserDto.LoginResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImgPath(user.getProfileImgPath())
                .token(token)
                .build();
    }

    /**
     * 3. 프로필 조회
     */
    @Transactional(readOnly = true)
    public UserDto.ProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        return UserDto.ProfileResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .profile(user.getProfile())
                .profileImgPath(user.getProfileImgPath())
                .createdAt(user.getCreatedAt())
                .build();
    }
}