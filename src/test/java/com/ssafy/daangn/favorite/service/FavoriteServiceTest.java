package com.ssafy.daangn.favorite.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.favorite.dto.FavoriteDto;
import com.ssafy.daangn.favorite.entity.Favorite;
import com.ssafy.daangn.favorite.repository.FavoriteRepository;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    private Member member;
    private Board board;

    @BeforeEach
    void setup() {
        member = Member.builder().memberId(1L).build();
        board = Board.builder().boardId(2L).build();
    }

    @Test
    void like_shouldSaveIfNotExist() {
        FavoriteDto dto = new FavoriteDto(1L, 2L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(boardRepository.findById(2L)).thenReturn(Optional.of(board));
        when(favoriteRepository.findByMemberAndBoard(member, board)).thenReturn(Optional.empty());

        favoriteService.like(dto);

        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void unlike_shouldDelete() {
        FavoriteDto dto = new FavoriteDto(1L, 2L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(boardRepository.findById(2L)).thenReturn(Optional.of(board));

        favoriteService.unlike(dto);

        verify(favoriteRepository).deleteByMemberAndBoard(member, board);
    }
}
