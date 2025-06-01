package com.ssafy.daangn.board.service;

import static org.assertj.core.api.Assertions.assertThat;
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

import com.ssafy.daangn.board.dto.request.BoardRequestDto;
import com.ssafy.daangn.board.dto.response.BoardResponseDto;
import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BoardService boardService;

    private Member member;
    private Board board;

    @BeforeEach
    void setup() {
        member = Member.builder()
                .memberId(1L)
                .membername("홍길동")
                .isDeleted(false)
                .build();

        board = Board.builder()
                .boardId(1L)
                .title("제목")
                .content("내용")
                .member(member)
                .isDeleted(false)
                .build();
    }

    @Test
    void 게시글_생성() {
        BoardRequestDto dto = new BoardRequestDto();
        dto.setMemberId(1L);
        dto.setTitle("제목");
        dto.setContent("내용");

        when(memberRepository.findByMemberIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(member));
        when(boardRepository.save(any(Board.class))).thenReturn(board);

        BoardResponseDto result = boardService.createBoard(dto);

        assertThat(result.getTitle()).isEqualTo("제목");
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    void 전체_게시글_조회() {
        when(boardRepository.findByIsDeletedFalse()).thenReturn(List.of(board));

        List<BoardResponseDto> results = boardService.getAllBoards();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("제목");
    }

    @Test
    void 삭제된_게시글_조회() {
        board.setIsDeleted(true);
        when(boardRepository.findByIsDeletedTrue()).thenReturn(List.of(board));

        List<BoardResponseDto> results = boardService.getDeletedBoards();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getBoardId()).isEqualTo(1L);
    }

    @Test
    void 회원별_게시글_조회() {
        when(boardRepository.findByMember_MemberIdAndIsDeletedFalse(1L)).thenReturn(List.of(board));

        List<BoardResponseDto> results = boardService.getBoardsByMember(1L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getMemberId()).isEqualTo(1L);
    }

    @Test
    void 제목으로_게시글_검색() {
        when(boardRepository.findByTitleContainingAndIsDeletedFalse("제")).thenReturn(List.of(board));

        List<BoardResponseDto> results = boardService.searchBoardsByTitle("제");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).contains("제");
    }

    @Test
    void 게시글_삭제() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        boardService.deleteBoard(1L);

        assertThat(board.getIsDeleted()).isTrue();
    }

    @Test
    void 게시글_수정() {
        BoardRequestDto dto = new BoardRequestDto();
        dto.setTitle("수정된 제목");
        dto.setContent("수정된 내용");

        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        BoardResponseDto result = boardService.updateBoard(1L, dto);

        assertThat(result.getTitle()).isEqualTo("수정된 제목");
        assertThat(result.getContent()).isEqualTo("수정된 내용");
    }
}
