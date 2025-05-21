package com.ssafy.Daangn.Service;

import com.ssafy.Daangn.Domain.User;
import com.ssafy.Daangn.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 전체 사용자 조회
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    // 사용자 ID로 사용자 조회
    public Optional<User> findUserByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    // 닉네임으로 사용자 검색
    public User findUsersByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    // 위치별 사용자 조회
    public List<User> findUsersByLocationId(Integer locationId) {
        return userRepository.findByLocationLocationId(locationId);
    }

    // 사용자 저장 (회원가입)
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // 사용자 정보 수정
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    // 사용자 삭제
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

}