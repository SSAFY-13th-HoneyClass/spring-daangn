package com.ssafy.daangn.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
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

    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setup() {
        member = Member.builder()
                .memberId(1L)
                .membername("user")
                .email("user@example.com")
                .password("pw")
                .build();
    }

    @Test
    void createBoardTest() {
        BoardRequestDto dto = new BoardRequestDto();
        dto.setMemberId(1L);
        dto.setTitle("title");
        dto.setContent("content");

        Board board = Board.builder()
                .boardId(1L)
                .member(member)
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(boardRepository.save(any(Board.class))).thenReturn(board);

        BoardResponseDto result = boardService.createBoard(dto);

        assertThat(result.getTitle()).isEqualTo("title");
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    void getAllBoardsTest() {
        when(boardRepository.findAll()).thenReturn(Collections.emptyList());
        List<BoardResponseDto> result = boardService.getAllBoards();
        assertThat(result).isEmpty();
    }
}
