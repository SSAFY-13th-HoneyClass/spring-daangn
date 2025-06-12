package com.ssafy.daangn.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.daangn.auth.dto.request.LoginRequestDto;
import com.ssafy.daangn.auth.dto.response.LoginResponseDto;
import com.ssafy.daangn.auth.jwt.JwtTokenProvider;
import com.ssafy.daangn.member.dto.request.MemberRequestDto;
import com.ssafy.daangn.member.dto.response.MemberResponseDto;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public MemberResponseDto signup(MemberRequestDto dto) {
        // 이메일 중복 검사
        if (memberRepository.findByEmailAndIsDeletedFalse(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Member newMember = Member.of(
                dto.getMembername(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()), // 비밀번호 해싱
                dto.getProfileUrl());

        return MemberResponseDto.from(memberRepository.save(newMember));
    }

    public LoginResponseDto login(LoginRequestDto requestDto) {
        Member member = memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        return new LoginResponseDto(accessToken, refreshToken);
    }

    public LoginResponseDto refreshAccessToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 유효하지 않습니다.");
        }

        String email = jwtTokenProvider.getEmail(refreshToken);

        // 회원이 실제 존재하는지 검증
        Member member = memberRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        // 새로운 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(email);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email); // 선택적: 기존 리프레시 재사용도 가능

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }

}
