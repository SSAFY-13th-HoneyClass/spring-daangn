package com.ssafy.Daangn.Service;

import com.ssafy.Daangn.Domain.RefreshToken;
import com.ssafy.Daangn.Domain.User;
import com.ssafy.Daangn.Dto.LoginRequestDto;
import com.ssafy.Daangn.Dto.SignupRequestDto;
import com.ssafy.Daangn.Dto.TokenResponseDto;
import com.ssafy.Daangn.Jwt.TokenProvider;
import com.ssafy.Daangn.Repository.RefreshTokenRepository;
import com.ssafy.Daangn.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenResponseDto login(LoginRequestDto loginRequest) {
        User user = userRepository.findByUserId(loginRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저"));

        if(!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("비밀번호 오류");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId().toString(), null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String accessToken = tokenProvider.createAccessToken(user.getId(), authentication);
        String refreshTokenStr = tokenProvider.createRefreshToken();

        // 발급 받으면 기존에 Refresh Token 은 삭제
        refreshTokenRepository.deleteByUserId(user.getId());

        // 새로 생성한 Refresh Token DB에 저장
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setExpireDate(LocalDateTime.now().plusSeconds(tokenProvider.getRefreshTokenExpirationTime()/1000));
        refreshTokenRepository.save(refreshToken);


        return TokenResponseDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .tokenExpiresIn(tokenProvider.getAccessTokenExpirationTime()/1000)   // 단위를 ms -> s 로
                .refreshTokenExpiresIn(tokenProvider.getRefreshTokenExpirationTime()/1000)
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();
    }

    public User singup(SignupRequestDto signupRequest) {
        if(userRepository.findByUserId(signupRequest.getUserId()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 ID");
        }

        User user = new User();
        user.setUserId(signupRequest.getUserId());
        user.setNickname(signupRequest.getNickname());
        user.setPassword(signupRequest.getPassword());
        user.setName(signupRequest.getName());

        user.setDegree(36.5);
        user.setLocation(null);
        user.setImage(null);


        return userRepository.save(user);
    }


    public TokenResponseDto refreshAccessToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(()-> new RuntimeException("유효하지 않은 Refresh Token"));

        // 리프레쉬 토큰이 만료되었을 경우
        if(refreshToken.isExpired()){
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh Token 만료");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(()-> new RuntimeException("사용자 없음"));

        // Refresh Token 으로 새로운 Access Token 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId().toString(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String newAccessToken = tokenProvider.createAccessToken(user.getId(), authentication);
        String newRefreshTokenStr = tokenProvider.createRefreshToken();

        refreshTokenRepository.delete(refreshToken);

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setUserId(user.getId());
        newRefreshToken.setToken(newRefreshTokenStr);
        newRefreshToken.setExpireDate(LocalDateTime.now().plusSeconds(tokenProvider.getRefreshTokenExpirationTime()/1000));
        refreshTokenRepository.save(newRefreshToken);

        return TokenResponseDto.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenStr)
                .tokenExpiresIn(tokenProvider.getAccessTokenExpirationTime()/1000)
                .refreshTokenExpiresIn(tokenProvider.getRefreshTokenExpirationTime()/1000)
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();

    }

    public void logoutByRefreshToken(String refreshTokenStr) {
        try{
            RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                    .orElseThrow(() -> new RuntimeException("유효하지 않는 Refresh Token"));
            refreshTokenRepository.deleteByUserId(refreshToken.getUserId());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
