package com.ssafy.daangn.member.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.daangn.member.dto.request.MemberRequestDto;
import com.ssafy.daangn.member.dto.response.MemberResponseDto;
import com.ssafy.daangn.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "회원 관련 API입니다.")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 생성", description = "새로운 회원을 생성합니다.")
    @PostMapping
    public ResponseEntity<MemberResponseDto> createMember(@RequestBody MemberRequestDto dto) {
        MemberResponseDto created = memberService.createMember(dto);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "전체 회원 조회", description = "삭제되지 않은 전체 회원 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<MemberResponseDto>> getAllMembers() {
        List<MemberResponseDto> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @Operation(summary = "삭제된 전체 회원 조회", description = "Soft delete 처리된 회원 목록을 조회합니다.")
    @GetMapping("/deleted")
    public ResponseEntity<List<MemberResponseDto>> getDeletedMembers() {
        List<MemberResponseDto> deletedMembers = memberService.getDeletedMembers();
        return ResponseEntity.ok(deletedMembers);
    }

    @Operation(summary = "회원 상세 조회", description = "특정 회원의 상세 정보를 조회합니다.")
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMemberById(@PathVariable Long memberId) {
        MemberResponseDto member = memberService.getMemberById(memberId);
        return ResponseEntity.ok(member);
    }

    @Operation(summary = "회원 삭제", description = "회원 정보를 삭제합니다. (Soft Delete 방식)")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정합니다.")
    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> updateMember(@PathVariable Long memberId, @RequestBody MemberRequestDto dto) {
        MemberResponseDto updated = memberService.updateMember(memberId, dto);
        return ResponseEntity.ok(updated);
    }
}
