package org.example.springboot.repository;

import org.example.springboot.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 등록 및 조회 테스트")
    public void createAndFindUsers() {
        // given
        User user1 = User.builder()
                .email("user1@example.com")
                .password("password1")
                .phone("010-1111-1111")
                .name("사용자1")
                .profile("안녕하세요 사용자1입니다.")
                .nickname("닉네임1")
                .profileImgPath("/images/profile1.jpg")
                .role("USER")
                .build();

        User user2 = User.builder()
                .email("user2@example.com")
                .password("password2")
                .phone("010-2222-2222")
                .name("사용자2")
                .profile("안녕하세요 사용자2입니다.")
                .nickname("닉네임2")
                .profileImgPath("/images/profile2.jpg")
                .role("USER")
                .build();

        User user3 = User.builder()
                .email("user3@example.com")
                .password("password3")
                .phone("010-3333-3333")
                .name("사용자3")
                .profile("안녕하세요 사용자3입니다.")
                .nickname("닉네임3")
                .profileImgPath("/images/profile3.jpg")
                .role("ADMIN")
                .build();

        // when
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        List<User> allUsers = userRepository.findAll();
        User foundUser = userRepository.findById(user1.getUserId()).orElse(null);
        User foundByEmail = userRepository.findByEmail("user2@example.com");
        User foundByUserId = userRepository.findByUserId(user3.getUserId());
        boolean existsEmail = userRepository.existsByEmail("user1@example.com");
        boolean existsNickname = userRepository.existsByNickname("닉네임3");
        boolean notExistsNickname = userRepository.existsByNickname("존재하지않는닉네임");

        // then
        assertThat(allUsers).hasSize(3);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("user1@example.com");

        assertThat(foundByEmail).isNotNull();
        assertThat(foundByEmail.getName()).isEqualTo("사용자2");

        assertThat(foundByUserId).isNotNull();
        assertThat(foundByUserId.getRole()).isEqualTo("ADMIN");

        assertThat(existsEmail).isTrue();

        assertThat(existsNickname).isTrue();
        assertThat(notExistsNickname).isFalse();
    }
}