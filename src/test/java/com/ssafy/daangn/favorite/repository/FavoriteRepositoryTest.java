package com.ssafy.daangn.favorite.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
    @DisplayName("회원과 게시글로 좋아요 저장 및 조회")
    void saveAndFindByMemberAndBoard() {
        Member member = memberRepository.save(Member.builder()
                .membername("user1")
                .email("user1@example.com")
                .password("pw")
                .build());

        Board board = boardRepository.save(Board.builder()
                .member(member)
                .title("게시글 제목")
                .content("내용")
                .build());

        Favorite favorite = Favorite.builder()
                .member(member)
                .board(board)
                .build();

        favoriteRepository.save(favorite);

        Optional<Favorite> found = favoriteRepository.findByMemberAndBoard(member, board);
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("특정 게시글에 눌린 좋아요 전체 조회")
    void findByBoard() {
        Member member1 = memberRepository.save(Member.builder()
                .membername("user1")
                .email("user1@example.com")
                .password("pw")
                .build());

        Member member2 = memberRepository.save(Member.builder()
                .membername("user2")
                .email("user2@example.com")
                .password("pw")
                .build());

        Board board = boardRepository.save(Board.builder()
                .member(member1)
                .title("같은 게시글")
                .content("내용")
                .build());

        favoriteRepository.save(Favorite.builder().member(member1).board(board).build());
        favoriteRepository.save(Favorite.builder().member(member2).board(board).build());

        List<Favorite> favorites = favoriteRepository.findByBoard(board);
        assertThat(favorites).hasSize(2);
    }

    @Test
    @DisplayName("특정 회원이 좋아요한 게시글 전체 조회")
    void findByMember() {
        Member member = memberRepository.save(Member.builder()
                .membername("user")
                .email("user@example.com")
                .password("pw")
                .build());

        Board board1 = boardRepository.save(Board.builder()
                .member(member)
                .title("제목1")
                .content("내용1")
                .build());

        Board board2 = boardRepository.save(Board.builder()
                .member(member)
                .title("제목2")
                .content("내용2")
                .build());

        favoriteRepository.save(Favorite.builder().member(member).board(board1).build());
        favoriteRepository.save(Favorite.builder().member(member).board(board2).build());

        List<Favorite> favorites = favoriteRepository.findByMember(member);
        assertThat(favorites).hasSize(2);
    }

    @Test
    @DisplayName("회원이 누른 좋아요 삭제")
    void deleteByMemberAndBoard() {
        Member member = memberRepository.save(Member.builder()
                .membername("user")
                .email("user@example.com")
                .password("pw")
                .build());

        Board board = boardRepository.save(Board.builder()
                .member(member)
                .title("삭제 대상")
                .content("내용")
                .build());

        favoriteRepository.save(Favorite.builder().member(member).board(board).build());
        favoriteRepository.deleteByMemberAndBoard(member, board);

        Optional<Favorite> deleted = favoriteRepository.findByMemberAndBoard(member, board);
        assertThat(deleted).isEmpty();
    }
}
