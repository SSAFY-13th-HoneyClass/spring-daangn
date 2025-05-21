package com.ssafy.daangn.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.daangn.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일로 조회
    Optional<Member> findByEmail(String email);

    // 삭제되지 않은 회원만 조회
    List<Member> findByIsDeletedFalse();

    // 이메일 중복 확인 (삭제되지 않은 회원 기준)
    Optional<Member> findByEmailAndIsDeletedFalse(String email);
}
