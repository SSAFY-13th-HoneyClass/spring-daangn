package com.ssafy.daangn.favorite.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.favorite.dto.FavoriteDto;
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

    @Transactional
    public void like(FavoriteDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        if (favoriteRepository.findByMemberAndBoard(member, board).isEmpty()) {
            Favorite favorite = Favorite.builder()
                    .member(member)
                    .board(board)
                    .build();
            favoriteRepository.save(favorite);
        }
    }

    @Transactional
    public void unlike(FavoriteDto dto) {
        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow();
        Board board = boardRepository.findById(dto.getBoardId()).orElseThrow();

        favoriteRepository.deleteByMemberAndBoard(member, board);
    }
}
