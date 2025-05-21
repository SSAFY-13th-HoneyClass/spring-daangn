package com.ssafy.daangn.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.daangn.member.dto.request.MemberRequestDto;
import com.ssafy.daangn.member.dto.response.MemberResponseDto;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    private MemberRequestDto dto;

    @BeforeEach
    void setup() {
        dto = new MemberRequestDto();
        dto.setMembername("홍길동");
        dto.setEmail("hong@example.com");
        dto.setPassword("pw");
        dto.setProfileUrl("url");
    }

    @Test
    void createMemberTest() {
        Member member = Member.builder()
                .memberId(1L)
                .membername(dto.getMembername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .profileUrl(dto.getProfileUrl())
                .build();

        when(memberRepository.findByEmailAndIsDeletedFalse(dto.getEmail()))
                .thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class)))
                .thenReturn(member);

        MemberResponseDto result = memberService.createMember(dto);
        assertThat(result.getEmail()).isEqualTo("hong@example.com");
        verify(memberRepository).save(any(Member.class));
    }
}
