package com.example.daangn.domain.user.service;

import com.example.daangn.domain.user.dto.UserRequestDto;
import com.example.daangn.domain.user.dto.UserResponseDto;
import com.example.daangn.domain.user.entity.User;
import com.example.daangn.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    //모든 유저 조회
    public List<User> findAll() {
        return userRepository.findAll();
    }

    //user id로 유저 조회
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    //user uid로 유저 조회
    public Optional<User> findByUID(Long uuid) {
        return userRepository.findByUuid(uuid);
    }

    /** 새로운 유저 추가 (회원가입)*/
    public UserResponseDto join(UserRequestDto userRequestDto) {
        // ID 중복 체크
        if (userRepository.existsById(userRequestDto.getId())) {
            throw new IllegalArgumentException("이미 존재하는 ID입니다: " + userRequestDto.getId());
        }

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(userRequestDto.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다: " + userRequestDto.getNickname());
        }

        // 비밀번호 암호화
        User user = userRequestDto.toEntity();
        user = User.builder()
                .id(user.getId())
                .password(passwordEncoder.encode(user.getPassword()))
                .name(user.getName())
                .nickname(user.getNickname())
                .phone((user.getPhone()))
                .profileImg(user.getProfileImg())
                .manner(user.getManner())
                .role(user.getRole() != null ? user.getRole() : User.Role.USER)
                .build();

        return UserResponseDto.fromEntity(userRepository.save(user));
    }

    //사용자 정보 수정
    public User update(User user) {
        return userRepository.save(user);
    }

    //user uid 기반 사용자 삭제
    public void delete(Long uuid) {
        userRepository.deleteById(uuid);
    }
}
