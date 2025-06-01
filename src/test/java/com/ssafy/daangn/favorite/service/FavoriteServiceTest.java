package com.ssafy.daangn.favorite.service;

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

import com.ssafy.daangn.board.dto.response.BoardResponseDto;
import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.favorite.dto.request.FavoriteRequestDto;
import com.ssafy.daangn.favorite.dto.response.FavoriteMemberResponseDto;
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
    void setUp() {
        member = Member.builder()
                .memberId(1L)
                .membername("홍길동")
                .profileUrl("https://example.com/hong.jpg")
                .isDeleted(false)
                .build();

        board = Board.builder()
                .boardId(100L)
                .title("제목")
                .content("내용")
                .isDeleted(false)
                .build();
    }

    @Test
    void toggle_ShouldSaveFavorite_IfNotExists() {
        // given
        FavoriteRequestDto dto = new FavoriteRequestDto(1L, 100L);

        when(memberRepository.findByMemberIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(member));
        when(boardRepository.findById(100L)).thenReturn(Optional.of(board));
        when(favoriteRepository.findByMemberAndBoard(member, board)).thenReturn(Optional.empty());

        // when
        favoriteService.toggle(dto);

        // then
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void toggle_ShouldDeleteFavorite_IfExists() {
        // given
        FavoriteRequestDto dto = new FavoriteRequestDto(1L, 100L);
        Favorite favorite = Favorite.builder().member(member).board(board).build();

        when(memberRepository.findByMemberIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(member));
        when(boardRepository.findById(100L)).thenReturn(Optional.of(board));
        when(favoriteRepository.findByMemberAndBoard(member, board)).thenReturn(Optional.of(favorite));

        // when
        favoriteService.toggle(dto);

        // then
        verify(favoriteRepository).delete(favorite);
    }

    @Test
    void getFavoriteCount_ShouldReturnSize() {
        when(boardRepository.findById(100L)).thenReturn(Optional.of(board));
        when(favoriteRepository.findByBoard(board)).thenReturn(List.of(
                new Favorite(), new Favorite(), new Favorite()
        ));

        Long count = favoriteService.getFavoriteCount(100L);
        assertThat(count).isEqualTo(3);
    }

    @Test
    void isFavorited_ShouldReturnTrue_IfFavoriteExists() {
        when(memberRepository.findByMemberIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(member));
        when(boardRepository.findById(100L)).thenReturn(Optional.of(board));
        when(favoriteRepository.findByMemberAndBoard(member, board)).thenReturn(Optional.of(new Favorite()));

        boolean result = favoriteService.isFavorited(1L, 100L);
        assertThat(result).isTrue();
    }

    @Test
    void isFavorited_ShouldReturnFalse_IfFavoriteDoesNotExist() {
        when(memberRepository.findByMemberIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(member));
        when(boardRepository.findById(100L)).thenReturn(Optional.of(board));
        when(favoriteRepository.findByMemberAndBoard(member, board)).thenReturn(Optional.empty());

        boolean result = favoriteService.isFavorited(1L, 100L);
        assertThat(result).isFalse();
    }

    @Test
    void getFavoriteBoardsByMember_ShouldReturnBoardResponseDtos() {
        Board board2 = Board.builder()
                .boardId(101L)
                .title("다른 게시글")
                .content("내용")
                .isDeleted(false)
                .build();

        Favorite fav1 = Favorite.builder().board(board).build();
        Favorite fav2 = Favorite.builder().board(board2).build();

        when(memberRepository.findByMemberIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(member));
        when(favoriteRepository.findByMember(member)).thenReturn(List.of(fav1, fav2));

        List<BoardResponseDto> result = favoriteService.getFavoriteBoardsByMember(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getBoardId()).isEqualTo(board.getBoardId());
        assertThat(result.get(1).getBoardId()).isEqualTo(board2.getBoardId());
    }

    @Test
    void getMembersWhoLikedBoard_ShouldReturnMemberDtoList() {
        Member member2 = Member.builder()
                .memberId(2L)
                .membername("이몽룡")
                .profileUrl("https://example.com/lee.jpg")
                .build();

        Favorite fav1 = Favorite.builder().member(member).board(board).build();
        Favorite fav2 = Favorite.builder().member(member2).board(board).build();

        when(boardRepository.findById(100L)).thenReturn(Optional.of(board));
        when(favoriteRepository.findByBoard(board)).thenReturn(List.of(fav1, fav2));

        List<FavoriteMemberResponseDto> result = favoriteService.getMembersWhoLikedBoard(100L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMemberId()).isEqualTo(1L);
        assertThat(result.get(1).getMembername()).isEqualTo("이몽룡");
    }
}
