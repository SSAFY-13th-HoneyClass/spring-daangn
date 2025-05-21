package com.ssafy.daangn.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ssafy.daangn.member.entity.Member;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("이메일로 회원 조회 및 중복 검증")
    void findByEmailTest() {
        Member member = Member.builder()
                .membername("test")
                .email("test@example.com")
                .password("pass")
                .build();
        memberRepository.save(member);

        Optional<Member> found = memberRepository.findByEmail("test@example.com");
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("삭제되지 않은 회원만 조회")
    void findByIsDeletedFalseTest() {
        Member member = Member.builder()
                .membername("active")
                .email("active@example.com")
                .password("pass")
                .build();
        memberRepository.save(member);

        assertThat(memberRepository.findByIsDeletedFalse()).isNotEmpty();
    }
}
