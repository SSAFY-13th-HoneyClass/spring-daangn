package com.ssafy.daangn.board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.daangn.board.dto.request.BoardRequestDto;
import com.ssafy.daangn.board.dto.response.BoardResponseDto;
import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    // 게시글 생성
    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto dto) {
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found or deleted"));

        Board board = Board.builder()
                .member(member)
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        return BoardResponseDto.from(boardRepository.save(board));
    }

    // 삭제되지 않은 게시글 전체 조회
    public List<BoardResponseDto> getAllBoards() {
        return boardRepository.findByIsDeletedFalse().stream()
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());
    }

    // 삭제된 게시글 전체 조회
    public List<BoardResponseDto> getDeletedBoards() {
        return boardRepository.findByIsDeletedTrue().stream()
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());
    }

    // 특정 회원이 작성한 게시글 조회
    public List<BoardResponseDto> getBoardsByMember(Long memberId) {
        return boardRepository.findByMember_MemberIdAndIsDeletedFalse(memberId).stream()
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());
    }

    // 제목에 키워드가 포함된 게시글 검색
    public List<BoardResponseDto> searchBoardsByTitle(String keyword) {
        return boardRepository.findByTitleContainingAndIsDeletedFalse(keyword).stream()
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());
    }

    // 게시글 삭제 (isDeleted 플래그 true로 설정)
    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));
        board.setIsDeleted(true);
    }

    // 게시글 수정
    @Transactional
    public BoardResponseDto updateBoard(Long boardId, BoardRequestDto dto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        if (dto.getTitle() != null) board.setTitle(dto.getTitle());
        if (dto.getContent() != null) board.setContent(dto.getContent());

        return BoardResponseDto.from(board);
    }

}