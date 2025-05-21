package com.ssafy.daangn.service;

import com.ssafy.daangn.domain.User;
import com.ssafy.daangn.dto.UserResponseDto;
import com.ssafy.daangn.dto.UserUpdateRequest;
import com.ssafy.daangn.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
        User user = userRepository.findByNo(no)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

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

        return new UserResponseDto(userRepository.save(updatedUser));
    }

    @Override
    public boolean delete(String id) {
        return false;
    }
}
