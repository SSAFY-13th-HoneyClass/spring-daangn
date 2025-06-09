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

    private Member createMember(String name, String email, boolean isDeleted) {
        Member member = Member.of(name, email, "pw1234", "https://example.com/profile.jpg");
        if (isDeleted) {
            member.markDeleted();
        }
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("회원 저장 및 ID 조회")
    void saveAndFindById() {
        Member saved = createMember("홍길동", "hong@example.com", false);

        Optional<Member> found = memberRepository.findById(saved.getMemberId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("hong@example.com");
    }

    @Test
    @DisplayName("삭제되지 않은 회원 조회")
    void findByIsDeletedFalse() {
        createMember("정상1", "user1@example.com", false);
        createMember("삭제1", "user2@example.com", true);

        List<Member> result = memberRepository.findByIsDeletedFalse();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("user1@example.com");
    }

    @Test
    @DisplayName("Soft Delete 회원만 조회")
    void findByIsDeletedTrue() {
        createMember("정상2", "user3@example.com", false);
        createMember("삭제2", "user4@example.com", true);

        List<Member> result = memberRepository.findByIsDeletedTrue();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("user4@example.com");
    }

    @Test
    @DisplayName("삭제되지 않은 회원 중 이메일로 조회")
    void findByEmailAndIsDeletedFalse() {
        createMember("유효회원", "valid@example.com", false);
        createMember("삭제회원", "valid@example.com", true); // 삭제된 회원은 제외

        Optional<Member> result = memberRepository.findByEmailAndIsDeletedFalse("valid@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("삭제되지 않은 회원 중 ID로 조회")
    void findByMemberIdAndIsDeletedFalse() {
        Member member = createMember("정상회원", "active@example.com", false);

        Optional<Member> found = memberRepository.findByMemberIdAndIsDeletedFalse(member.getMemberId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("active@example.com");
    }

    @Test
    @DisplayName("삭제된 회원은 findByMemberIdAndIsDeletedFalse로 조회되지 않음")
    void findByMemberIdAndIsDeletedFalse_deletedCase() {
        Member member = createMember("삭제된회원", "deleted@example.com", true);

        Optional<Member> result = memberRepository.findByMemberIdAndIsDeletedFalse(member.getMemberId());

        assertThat(result).isNotPresent();
    }
}
