package com.ssafy.daangn.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.ssafy.daangn.member.dto.request.MemberRequestDto;
import com.ssafy.daangn.member.dto.response.MemberResponseDto;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

class MemberServiceTest {

    private MemberRepository memberRepository;
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberRepository = Mockito.mock(MemberRepository.class);
        memberService = new MemberService(memberRepository);
    }

    @Test
    @DisplayName("회원 생성 - 성공")
    void createMember_success() {
        MemberRequestDto dto = new MemberRequestDto();
        dto.setMembername("홍길동");
        dto.setEmail("hong@example.com");
        dto.setPassword("pw1234");
        dto.setProfileUrl("https://img.jpg");

        given(memberRepository.findByEmailAndIsDeletedFalse(dto.getEmail()))
                .willReturn(Optional.empty());

        Member saved = Member.of(dto.getMembername(), dto.getEmail(), dto.getPassword(), dto.getProfileUrl());
        given(memberRepository.save(any(Member.class))).willReturn(saved);

        MemberResponseDto result = memberService.createMember(dto);

        assertThat(result.getEmail()).isEqualTo("hong@example.com");
        assertThat(result.getMembername()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("회원 생성 - 실패 (중복 이메일)")
    void createMember_duplicateEmail() {
        MemberRequestDto dto = new MemberRequestDto();
        dto.setEmail("exist@example.com");

        given(memberRepository.findByEmailAndIsDeletedFalse(dto.getEmail()))
                .willReturn(Optional.of(Member.of("기존", dto.getEmail(), "pw", null)));

        assertThatThrownBy(() -> memberService.createMember(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

    @Test
    @DisplayName("삭제되지 않은 전체 회원 조회")
    void getAllMembers() {
        Member m1 = Member.of("a", "a@example.com", "pw", null);
        Member m2 = Member.of("b", "b@example.com", "pw", null);

        given(memberRepository.findByIsDeletedFalse()).willReturn(List.of(m1, m2));

        List<MemberResponseDto> result = memberService.getAllMembers();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Soft Deleted 회원 목록 조회")
    void getDeletedMembers() {
        Member m1 = Member.of("삭제된 유저", "del@example.com", "pw", null);
        m1.markDeleted();

        given(memberRepository.findByIsDeletedTrue()).willReturn(List.of(m1));

        List<MemberResponseDto> result = memberService.getDeletedMembers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("del@example.com");
    }

    @Test
    @DisplayName("회원 ID로 조회 - 성공")
    void getMemberById_success() {
        Member member = Member.of("이몽룡", "lee@example.com", "pw", null);

        given(memberRepository.findByMemberIdAndIsDeletedFalse(1L))
                .willReturn(Optional.of(member));

        MemberResponseDto result = memberService.getMemberById(1L);

        assertThat(result.getMembername()).isEqualTo("이몽룡");
    }

    @Test
    @DisplayName("회원 ID로 조회 - 실패")
    void getMemberById_fail() {
        given(memberRepository.findByMemberIdAndIsDeletedFalse(1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMemberById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("회원 정보 수정 - 성공")
    void updateMember_success() {
        Member member = Member.of("원래이름", "origin@example.com", "pw", null);

        given(memberRepository.findByMemberIdAndIsDeletedFalse(1L))
                .willReturn(Optional.of(member));
        given(memberRepository.findByEmailAndIsDeletedFalse("new@example.com"))
                .willReturn(Optional.empty());

        MemberRequestDto dto = new MemberRequestDto();
        dto.setMembername("새이름");
        dto.setEmail("new@example.com");
        dto.setPassword("newpw");
        dto.setProfileUrl("https://img.png");

        MemberResponseDto result = memberService.updateMember(1L, dto);

        assertThat(result.getMembername()).isEqualTo("새이름");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("회원 정보 수정 - 실패 (중복 이메일)")
    void updateMember_duplicateEmail() {
        Member member = Member.of("기존", "origin@example.com", "pw", null);

        given(memberRepository.findByMemberIdAndIsDeletedFalse(1L))
                .willReturn(Optional.of(member));
        given(memberRepository.findByEmailAndIsDeletedFalse("dup@example.com"))
                .willReturn(Optional.of(Member.of("다른", "dup@example.com", "pw", null)));

        MemberRequestDto dto = new MemberRequestDto();
        dto.setEmail("dup@example.com");

        assertThatThrownBy(() -> memberService.updateMember(1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("회원 삭제 (Soft Delete)")
    void deleteMember() {
        Member member = Member.of("삭제할회원", "delete@example.com", "pw", null);

        given(memberRepository.findByMemberIdAndIsDeletedFalse(1L))
                .willReturn(Optional.of(member));

        memberService.deleteMember(1L);

        assertThat(member.getIsDeleted()).isTrue();
    }
}
