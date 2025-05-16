package org.example.springboot.service;

import org.example.springboot.domain.User;
import org.example.springboot.dto.UserDto;
import org.example.springboot.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 - 성공")
    public void registerUser_Success() {
        // given
        UserDto.RegisterRequest requestDto = UserDto.RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .phone("010-1234-5678")
                .name("테스트유저")
                .profile("안녕하세요")
                .nickname("테스트닉네임")
                .profileImgPath("/images/default.jpg")
                .build();

        // when
        Long userId = userService.registerUser(requestDto);

        // then
        User savedUser = userRepository.findByUserId(userId);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getNickname()).isEqualTo("테스트닉네임");

        // 비밀번호가 암호화되어 저장되었는지 확인
        assertThat(passwordEncoder.matches("password123", savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("회원가입 - 이메일 중복 실패")
    public void registerUser_EmailExists_Fail() {
        // given
        // 첫 번째 유저 등록
        UserDto.RegisterRequest request1 = UserDto.RegisterRequest.builder()
                .email("duplicate@example.com")
                .password("password123")
                .phone("010-1111-1111")
                .name("테스트유저1")
                .profile("안녕하세요")
                .nickname("테스트닉네임1")
                .profileImgPath("/images/default1.jpg")
                .build();
        userService.registerUser(request1);

        // 중복 이메일로 두 번째 유저 등록
        UserDto.RegisterRequest request2 = UserDto.RegisterRequest.builder()
                .email("duplicate@example.com") // 동일한 이메일
                .password("password456")
                .phone("010-2222-2222")
                .name("테스트유저2")
                .profile("안녕하세요")
                .nickname("테스트닉네임2")
                .profileImgPath("/images/default2.jpg")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.registerUser(request2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

    @Test
    @DisplayName("로그인 - 성공")
    public void login_Success() {
        // given
        String email = "login@example.com";
        String password = "password123";

        // 유저 등록
        UserDto.RegisterRequest registerRequest = UserDto.RegisterRequest.builder()
                .email(email)
                .password(password)
                .phone("010-1234-5678")
                .name("로그인테스트")
                .profile("안녕하세요")
                .nickname("로그인닉네임")
                .profileImgPath("/images/login.jpg")
                .build();
        userService.registerUser(registerRequest);

        // 로그인 요청
        UserDto.LoginRequest loginRequest = UserDto.LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        // when
        UserDto.LoginResponse response = userService.login(loginRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getNickname()).isEqualTo("로그인닉네임");
        assertThat(response.getToken()).isNotNull();
    }

    @Test
    @DisplayName("로그인 - 비밀번호 불일치 실패")
    public void login_WrongPassword_Fail() {
        // given
        String email = "password@example.com";
        String password = "correctPassword";

        // 유저 등록
        UserDto.RegisterRequest registerRequest = UserDto.RegisterRequest.builder()
                .email(email)
                .password(password)
                .phone("010-1234-5678")
                .name("비밀번호테스트")
                .profile("안녕하세요")
                .nickname("비밀번호닉네임")
                .profileImgPath("/images/password.jpg")
                .build();
        userService.registerUser(registerRequest);

        // 잘못된 비밀번호로 로그인 요청
        UserDto.LoginRequest loginRequest = UserDto.LoginRequest.builder()
                .email(email)
                .password("wrongPassword")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("사용자 프로필 조회 - 성공")
    public void getUserProfile_Success() {
        // given
        UserDto.RegisterRequest registerRequest = UserDto.RegisterRequest.builder()
                .email("profile@example.com")
                .password("password123")
                .phone("010-1234-5678")
                .name("프로필테스트")
                .profile("안녕하세요 프로필입니다")
                .nickname("프로필닉네임")
                .profileImgPath("/images/profile.jpg")
                .build();
        Long userId = userService.registerUser(registerRequest);

        // when
        UserDto.ProfileResponse profileResponse = userService.getUserProfile(userId);

        // then
        assertThat(profileResponse).isNotNull();
        assertThat(profileResponse.getUserId()).isEqualTo(userId);
        assertThat(profileResponse.getEmail()).isEqualTo("profile@example.com");
        assertThat(profileResponse.getName()).isEqualTo("프로필테스트");
        assertThat(profileResponse.getNickname()).isEqualTo("프로필닉네임");
        assertThat(profileResponse.getProfile()).isEqualTo("안녕하세요 프로필입니다");
        assertThat(profileResponse.getProfileImgPath()).isEqualTo("/images/profile.jpg");
    }
}