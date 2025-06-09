package com.ssafy.daangn.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.daangn.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 삭제되지 않은 회원 중 이메일로 조회
    Optional<Member> findByEmailAndIsDeletedFalse(String email);

    // 삭제되지 않은 회원 전체 조회
    List<Member> findByIsDeletedFalse();

    // 삭제된 회원 전체 조회
    List<Member> findByIsDeletedTrue();

    // 삭제되지 않은 회원 중 ID로 조회
    Optional<Member> findByMemberIdAndIsDeletedFalse(Long memberId);
}
