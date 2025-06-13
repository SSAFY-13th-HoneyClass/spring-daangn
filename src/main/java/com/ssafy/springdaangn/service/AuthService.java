package com.ssafy.springdaangn.service;

import com.ssafy.springdaangn.domain.User;
import com.ssafy.springdaangn.dto.LoginRequestDto;
import com.ssafy.springdaangn.dto.SignupRequestDto;
import com.ssafy.springdaangn.dto.TokenResponseDto;
import com.ssafy.springdaangn.jwt.TokenProvider;
import com.ssafy.springdaangn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
   // private final RefreshTokenRepository refreshTokenRepository;

    public TokenResponseDto login(LoginRequestDto loginRequest) {
        User user = userRepository.findByid(loginRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저"));

        if(!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("비밀번호 오류");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId().toString(), null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String accessToken = tokenProvider.createToken(user.getUserId(), authentication);
       // String refreshTokenStr = tokenProvider.createRefreshToken();

        // 발급 받으면 기존에 Refresh Token 은 삭제
        //refreshTokenRepository.deleteByUserId(user.getId());

        // 새로 생성한 Refresh Token DB에 저장
//        RefreshToken refreshToken = new RefreshToken();
//        refreshToken.setUserId(user.getId());
//        refreshToken.setToken(refreshTokenStr);
//        refreshToken.setExpireDate(LocalDateTime.now().plusSeconds(tokenProvider.getRefreshTokenExpirationTime()/1000));
//        refreshTokenRepository.save(refreshToken);


        return TokenResponseDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
               // .refreshToken(refreshTokenStr)
                .tokenExpiresIn(tokenProvider.getAccessTokenExpirationTime()/1000)   // 단위를 ms -> s 로
               // .refreshTokenExpiresIn(tokenProvider.getRefreshTokenExpirationTime()/1000)
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .build();
    }

    public User singup(SignupRequestDto signupRequest) {
        if(userRepository.findByid(signupRequest.getUserId()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 ID");
        }

        User user = new User();
        user.setId(signupRequest.getUserId());
        user.setNickname(signupRequest.getNickname());
        user.setPassword(signupRequest.getPassword());

        return userRepository.save(user);
    }
}
