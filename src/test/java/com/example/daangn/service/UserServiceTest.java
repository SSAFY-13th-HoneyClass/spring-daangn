package com.example.daangn.service;

import com.example.daangn.domain.user.entity.User;
import com.example.daangn.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    private User testUser1;

    @BeforeEach
    public void setup(){
        // 테스트용 사용자 생성
        User u1 = User.builder()
                .id("testuser1")
                .password("password")
                .name("Test User1")
                .nickname("tester1")
                .phone("01012345678")
                .manner(new BigDecimal("36.5"))
                .role("USER")
                .build();

        testUser1 = userService.join(u1);
    }

    @Test
    public void test(){
        //given
        //같은 Id를 가진 user를 삽입
        User u2 = User.builder()
                .id("testuser1")
                .password("password")
                .name("Test User2")
                .nickname("tester2")
                .phone("01012345678")
                .manner(new BigDecimal("36.5"))
                .role("USER")
                .build();


        //when
        User newUser = userService.join(u2);

        //then
        if(newUser == null){
            System.out.println("회원 가입 실패: 이미 존재하는 유저입니다.");
            return;
        }

        System.out.println("회원 가입 성공!");
    }
}
