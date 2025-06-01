package com.ssafy.daangn.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
    @DisplayName("이메일로 삭제되지 않은 회원 조회")
    void findByEmailAndIsDeletedFalse() {
        Member member = Member.builder()
                .membername("user1")
                .email("user1@example.com")
                .password("pw")
                .isDeleted(false)
                .build();
        memberRepository.save(member);

        Optional<Member> found = memberRepository.findByEmailAndIsDeletedFalse("user1@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("user1@example.com");
    }

    @Test
    @DisplayName("삭제되지 않은 전체 회원 조회")
    void findByIsDeletedFalse() {
        Member active1 = memberRepository.save(Member.builder()
                .membername("active1").email("a1@x.com").password("pw").isDeleted(false).build());
        Member active2 = memberRepository.save(Member.builder()
                .membername("active2").email("a2@x.com").password("pw").isDeleted(false).build());
        Member deleted = memberRepository.save(Member.builder()
                .membername("deleted").email("d@x.com").password("pw").isDeleted(true).build());

        List<Member> actives = memberRepository.findByIsDeletedFalse();
        assertThat(actives).containsExactlyInAnyOrder(active1, active2);
        assertThat(actives).doesNotContain(deleted);
    }

    @Test
    @DisplayName("삭제된 전체 회원 조회")
    void findByIsDeletedTrue() {
        Member active = memberRepository.save(Member.builder()
                .membername("active").email("active@x.com").password("pw").isDeleted(false).build());
        Member deleted1 = memberRepository.save(Member.builder()
                .membername("deleted1").email("d1@x.com").password("pw").isDeleted(true).build());
        Member deleted2 = memberRepository.save(Member.builder()
                .membername("deleted2").email("d2@x.com").password("pw").isDeleted(true).build());

        List<Member> deleteds = memberRepository.findByIsDeletedTrue();
        assertThat(deleteds).containsExactlyInAnyOrder(deleted1, deleted2);
        assertThat(deleteds).doesNotContain(active);
    }

    @Test
    @DisplayName("ID로 삭제되지 않은 회원 조회")
    void findByMemberIdAndIsDeletedFalse() {
        Member active = memberRepository.save(Member.builder()
                .membername("active").email("active@x.com").password("pw").isDeleted(false).build());
        Member deleted = memberRepository.save(Member.builder()
                .membername("deleted").email("deleted@x.com").password("pw").isDeleted(true).build());

        Optional<Member> found = memberRepository.findByMemberIdAndIsDeletedFalse(active.getMemberId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("active@x.com");

        Optional<Member> notFound = memberRepository.findByMemberIdAndIsDeletedFalse(deleted.getMemberId());
        assertThat(notFound).isNotPresent();
    }
}
