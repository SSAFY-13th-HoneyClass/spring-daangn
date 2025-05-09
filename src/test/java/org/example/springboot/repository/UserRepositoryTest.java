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
        // 테스트 데이터 생성
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1.setPhone("010-1111-1111");
        user1.setName("사용자1");
        user1.setProfile("안녕하세요 사용자1입니다.");
        user1.setNickname("닉네임1");
        user1.setProfileImgPath("/images/profile1.jpg");
        user1.setRole("USER");

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        user2.setPhone("010-2222-2222");
        user2.setName("사용자2");
        user2.setProfile("안녕하세요 사용자2입니다.");
        user2.setNickname("닉네임2");
        user2.setProfileImgPath("/images/profile2.jpg");
        user2.setRole("USER");

        User user3 = new User();
        user3.setEmail("user3@example.com");
        user3.setPassword("password3");
        user3.setPhone("010-3333-3333");
        user3.setName("사용자3");
        user3.setProfile("안녕하세요 사용자3입니다.");
        user3.setNickname("닉네임3");
        user3.setProfileImgPath("/images/profile3.jpg");
        user3.setRole("ADMIN");

        // 사용자 저장
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // 전체 사용자 조회 테스트
        List<User> allUsers = userRepository.findAll();
        assertThat(allUsers).hasSize(3);

        // ID로 사용자 조회 테스트
        User foundUser = userRepository.findById(user1.getUserId()).orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("user1@example.com");

        // 이메일로 사용자 조회 테스트
        User foundByEmail = userRepository.findByEmail("user2@example.com");
        assertThat(foundByEmail).isNotNull();
        assertThat(foundByEmail.getName()).isEqualTo("사용자2");

        // 사용자 ID로 조회 테스트
        User foundByUserId = userRepository.findByUserId(user3.getUserId());
        assertThat(foundByUserId).isNotNull();
        assertThat(foundByUserId.getRole()).isEqualTo("ADMIN");

        // 이메일 존재 여부 테스트
        boolean existsEmail = userRepository.existsByEmail("user1@example.com");
        assertThat(existsEmail).isTrue();

        // 닉네임 존재 여부 테스트
        boolean existsNickname = userRepository.existsByNickname("닉네임3");
        assertThat(existsNickname).isTrue();
        boolean notExistsNickname = userRepository.existsByNickname("존재하지않는닉네임");
        assertThat(notExistsNickname).isFalse();
    }
}