package com.ssafy.daangn.favorite.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.daangn.board.dto.response.BoardResponseDto;
import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.favorite.dto.request.FavoriteRequestDto;
import com.ssafy.daangn.favorite.dto.response.FavoriteMemberResponseDto;
import com.ssafy.daangn.favorite.entity.Favorite;
import com.ssafy.daangn.favorite.repository.FavoriteRepository;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    // 좋아요 토글 - 없으면 추가, 있으면 삭제
    @Transactional
    public void toggle(FavoriteRequestDto dto) {
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        favoriteRepository.findByMemberAndBoard(member, board)
                .ifPresentOrElse(
                        favoriteRepository::delete,
                        () -> favoriteRepository.save(Favorite.builder()
                                .member(member)
                                .board(board)
                                .build())
                );
    }

    // 게시글에 대한 좋아요 개수 조회
    public Long getFavoriteCount(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));
        return (long) favoriteRepository.findByBoard(board).size();
    }

    // 특정 유저가 해당 게시글을 좋아요했는지 확인
    public boolean isFavorited(Long memberId, Long boardId) {
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        return favoriteRepository.findByMemberAndBoard(member, board).isPresent();
    }

    // 특정 유저가 좋아요한 게시글 목록 조회 (삭제된 게시글 제외)
    public List<BoardResponseDto> getFavoriteBoardsByMember(Long memberId) {
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return favoriteRepository.findByMember(member).stream()
                .map(Favorite::getBoard)
                .filter(board -> !board.getIsDeleted())
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());
    }

    // 게시글을 좋아요한 회원 목록
    @Transactional(readOnly = true)
    public List<FavoriteMemberResponseDto> getMembersWhoLikedBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));
        return favoriteRepository.findByBoard(board).stream()
                .map(fav -> FavoriteMemberResponseDto.from(fav.getMember()))
                .collect(Collectors.toList());
    }

}
