package com.example.daangn.repository;

import com.example.daangn.domain.Role;
import com.example.daangn.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 3명 저장")
    void saveUsers() {
        // given
        User user1 = User.builder()
                .name("홍정인")
                .email("sss@ss.ss")
                .password("1234")
                .role(Role.ADMIN)
                .build();
        User user2 = User.builder()
                .name("이휘")
                .email("hwi@w.w")
                .password("1234")
                .role(Role.USER)
                .build();
        User user3 = User.builder()
                .name("이주현")
                .email("math@m.m")
                .password("1234")
                .role(Role.USER)
                .build();
        // when
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        Optional<User> found = userRepository.findByEmail("sss@ss.ss");
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("홍정인");
        assertThat(found.get().getRole()).isEqualTo(Role.ADMIN);
    }
}
