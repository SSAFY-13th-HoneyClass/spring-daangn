package com.ssafy.daangn.repositoryTest;

import com.ssafy.daangn.domain.Users;
import com.ssafy.daangn.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testInsertUser() {
        Users user = Users.builder()
                .name("테스트 유저")
                .id("test123")
                .password("password")
                .email("test@example.com")
                .phone("010-0000-0000")
                .address("서울시 어딘가")
                .temperature(36.5)
                .addressDetail("302호")
                .nickname("testnick")
                .latitude(37.12345)
                .longitude(127.12345)
                .build();

        Users savedUser = userRepository.save(user);
        System.out.println("저장된 사용자 ID: " + savedUser.getNo());
    }
}
