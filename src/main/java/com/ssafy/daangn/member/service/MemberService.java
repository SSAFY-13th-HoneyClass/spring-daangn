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
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 생성
    @Transactional
    public MemberResponseDto createMember(MemberRequestDto dto) {
        // 중복 이메일 중복 검사
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

    // 전체 회원 목록 조회 (삭제되지 않은 회원만)
    public List<MemberResponseDto> getAllMembers() {
        return memberRepository.findByIsDeletedFalse().stream()
                .map(MemberResponseDto::from)
                .collect(Collectors.toList());
    }

    // 삭제된 회원 전체 조회
    public List<MemberResponseDto> getDeletedMembers() {
        return memberRepository.findByIsDeletedTrue().stream()
                .map(MemberResponseDto::from)
                .collect(Collectors.toList());
    }

    // 회원 ID로 단일 회원 조회 (삭제되지 않은 회원만)
    public MemberResponseDto getMemberById(Long memberId) {
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return MemberResponseDto.from(member);
    }

    // 회원 삭제 (soft delete: isDeleted = true)
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        member.markDeleted();
    }

    // 회원 정보 수정 (필드별 null 체크 후 업데이트)
    @Transactional
    public MemberResponseDto updateMember(Long memberId, MemberRequestDto dto) {
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (dto.getMembername() != null) member.setMembername(dto.getMembername());

        // 이메일 변경된 경우 중복 검사
        if (dto.getEmail() != null && !dto.getEmail().equals(member.getEmail())) {
            boolean emailExists = memberRepository.findByEmailAndIsDeletedFalse(dto.getEmail()).isPresent();
            if (emailExists) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
            member.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null) member.setPassword(dto.getPassword());
        if (dto.getProfileUrl() != null) member.setProfileUrl(dto.getProfileUrl());

        return MemberResponseDto.from(member);
    }

}
