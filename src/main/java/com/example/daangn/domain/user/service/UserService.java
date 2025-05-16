package com.example.daangn.domain.user.service;

import com.example.daangn.domain.user.entity.User;
import com.example.daangn.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

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
    public User join(User user) {
        return userRepository.save(user);
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
