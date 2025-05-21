package com.ssafy.daangn.favorite.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.favorite.entity.Favorite;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
class FavoriteRepositoryTest {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("회원과 게시글 기준으로 좋아요 저장 및 중복 확인")
    void saveAndFindFavorite() {
        Member member = memberRepository.save(Member.builder()
                .membername("홍길동")
                .email("hong@example.com")
                .password("pass")
                .build());

        Board board = boardRepository.save(Board.builder()
                .member(member)
                .title("Title")
                .content("Content")
                .build());

        Favorite favorite = Favorite.builder().member(member).board(board).build();
        favoriteRepository.save(favorite);

        Optional<Favorite> found = favoriteRepository.findByMemberAndBoard(member, board);
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("좋아요 삭제 테스트")
    void deleteFavorite() {
        Member member = memberRepository.save(Member.builder()
                .membername("유저")
                .email("test@x.com")
                .password("pw")
                .build());

        Board board = boardRepository.save(Board.builder()
                .member(member)
                .title("제목")
                .content("내용")
                .build());

        Favorite favorite = Favorite.builder().member(member).board(board).build();
        favoriteRepository.save(favorite);

        favoriteRepository.deleteByMemberAndBoard(member, board);
        assertThat(favoriteRepository.findByMemberAndBoard(member, board)).isEmpty();
    }
}
