package com.ssafy.daangn.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member;

    @BeforeEach
    void setup() {
        member = Member.builder()
                .memberId(1L)
                .membername("홍길동")
                .email("hong@example.com")
                .password("password")
                .profileUrl("url")
                .isDeleted(false)
                .build();
    }

    @Test
    void createMember_성공() {
        MemberRequestDto dto = new MemberRequestDto();
        dto.setMembername("홍길동");
        dto.setEmail("hong@example.com");
        dto.setPassword("password");
        dto.setProfileUrl("url");

        when(memberRepository.findByEmailAndIsDeletedFalse(dto.getEmail()))
                .thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class)))
                .thenReturn(member);

        MemberResponseDto result = memberService.createMember(dto);

        assertThat(result.getEmail()).isEqualTo("hong@example.com");
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void createMember_중복이메일_예외() {
        MemberRequestDto dto = new MemberRequestDto();
        dto.setEmail("hong@example.com");

        when(memberRepository.findByEmailAndIsDeletedFalse(dto.getEmail()))
                .thenReturn(Optional.of(member));

        assertThatThrownBy(() -> memberService.createMember(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

    @Test
    void getAllMembers_정상조회() {
        when(memberRepository.findByIsDeletedFalse())
                .thenReturn(List.of(member));

        List<MemberResponseDto> results = memberService.getAllMembers();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEmail()).isEqualTo("hong@example.com");
    }

    @Test
    void getMemberById_존재함() {
        when(memberRepository.findByMemberIdAndIsDeletedFalse(1L))
                .thenReturn(Optional.of(member));

        MemberResponseDto result = memberService.getMemberById(1L);

        assertThat(result.getMemberId()).isEqualTo(1L);
    }

    @Test
    void getMemberById_없음_예외() {
        when(memberRepository.findByMemberIdAndIsDeletedFalse(2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMemberById(2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void deleteMember_정상삭제() {
        when(memberRepository.findByMemberIdAndIsDeletedFalse(1L))
                .thenReturn(Optional.of(member));

        memberService.deleteMember(1L);

        assertThat(member.getIsDeleted()).isTrue();
    }

    @Test
    void updateMember_정상수정() {
        MemberRequestDto dto = new MemberRequestDto();
        dto.setMembername("이순신");
        dto.setEmail("new@example.com");
        dto.setPassword("newpw");
        dto.setProfileUrl("newUrl");

        when(memberRepository.findByMemberIdAndIsDeletedFalse(1L))
                .thenReturn(Optional.of(member));
        when(memberRepository.findByEmailAndIsDeletedFalse("new@example.com"))
                .thenReturn(Optional.empty());

        MemberResponseDto result = memberService.updateMember(1L, dto);

        assertThat(result.getMembername()).isEqualTo("이순신");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getProfileUrl()).isEqualTo("newUrl");
    }

    @Test
    void updateMember_이메일중복_예외() {
        Member other = Member.builder()
                .memberId(2L)
                .email("duplicate@example.com")
                .isDeleted(false)
                .build();

        MemberRequestDto dto = new MemberRequestDto();
        dto.setEmail("duplicate@example.com");

        when(memberRepository.findByMemberIdAndIsDeletedFalse(1L))
                .thenReturn(Optional.of(member));
        when(memberRepository.findByEmailAndIsDeletedFalse("duplicate@example.com"))
                .thenReturn(Optional.of(other));

        assertThatThrownBy(() -> memberService.updateMember(1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용 중인 이메일입니다.");
    }
}
