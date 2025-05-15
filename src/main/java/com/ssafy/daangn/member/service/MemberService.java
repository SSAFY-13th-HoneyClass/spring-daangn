package com.ssafy.daangn.member.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.daangn.member.dto.request.MemberRequestDto;
import com.ssafy.daangn.member.dto.response.MemberResponseDto;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponseDto createMember(MemberRequestDto dto) {
        if (memberRepository.findByEmailAndIsDeletedFalse(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Member member = Member.builder()
                .membername(dto.getMembername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .profileUrl(dto.getProfileUrl())
                .build();

        return MemberResponseDto.from(memberRepository.save(member));
    }

    public List<MemberResponseDto> getAllMembers() {
        return memberRepository.findByIsDeletedFalse().stream()
                .map(MemberResponseDto::from)
                .collect(Collectors.toList());
    }
}
