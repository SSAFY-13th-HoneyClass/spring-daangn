package com.ssafy.daangn.service;

import com.ssafy.daangn.domain.User;
import com.ssafy.daangn.dto.UserResponseDto;
import com.ssafy.daangn.dto.UserUpdateRequest;
import com.ssafy.daangn.exception.UserNotFoundException;
import com.ssafy.daangn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByNo(Long no) {
        return userRepository.findByNo(no);
    }

    @Override
    public Optional<User> findByIdAndEmail(String id, String email) {
        return userRepository.findByIdAndEmail(id, email);
    }

    @Override
    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    public Optional<User> findByIdAndPassword(String id, String password) {
        return userRepository.findByIdAndPassword(id, password);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public UserResponseDto update(Long no, UserUpdateRequest request) {
        // ✅ 예외를 던지면 GlobalExceptionHandler가 처리
        User user = userRepository.findByNo(no)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + no));

        User updatedUser = user.toBuilder()
                .name(request.getName())
                .password(request.getPassword())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .temperature(request.getTemperature())
                .addressDetail(request.getAddressDetail())
                .nickname(request.getNickname())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

        User saved = userRepository.save(updatedUser);
        // ✅ 정적 팩토리 메서드 사용
        return UserResponseDto.from(saved);
    }

    @Override
    public boolean delete(String id) {
        return false;
    }
}