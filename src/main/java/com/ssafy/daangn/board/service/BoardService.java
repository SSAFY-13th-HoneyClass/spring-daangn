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

    public List<BoardResponseDto> getAllBoards() {
        return boardRepository.findByIsDeletedFalse().stream()
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<BoardResponseDto> getDeletedBoards() {
        return boardRepository.findByIsDeletedTrue().stream()
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<BoardResponseDto> getBoardsByMember(Long memberId) {
        return boardRepository.findByMember_MemberIdAndIsDeletedFalse(memberId).stream()
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<BoardResponseDto> searchBoardsByTitle(String keyword) {
        return boardRepository.findByTitleContainingAndIsDeletedFalse(keyword).stream()
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));
        board.setIsDeleted(true);
    }

    @Transactional
    public BoardResponseDto updateBoard(Long boardId, BoardRequestDto dto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        if (dto.getTitle() != null) board.setTitle(dto.getTitle());
        if (dto.getContent() != null) board.setContent(dto.getContent());

        return BoardResponseDto.from(board);
    }

}
