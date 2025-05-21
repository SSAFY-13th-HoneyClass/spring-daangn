package com.ssafy.daangn.board.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("게시글 저장 및 연관된 유저 확인")
    void saveAndLoadBoard() {
        Member member = Member.builder()
                .membername("user1")
                .email("user1@example.com")
                .password("1234")
                .build();
        memberRepository.save(member);

        Board board = Board.builder()
                .member(member)
                .title("title")
                .content("content")
                .build();
        boardRepository.save(board);

        em.clear(); // 영속성 컨텍스트 초기화 → Lazy Loading 확인용

        Board found = boardRepository.findById(board.getBoardId()).orElseThrow();
        assertThat(found.getMember().getEmail()).isEqualTo("user1@example.com"); // Lazy loading 정상 동작 여부 확인
    }

    @Test
    @DisplayName("isDeleted = false 조건으로 게시글 필터링 조회")
    void findByIsDeletedFalse() {
        List<Board> boards = boardRepository.findByIsDeletedFalse();
        assertThat(boards).isNotNull();
    }

    @Test
    @DisplayName("제목 키워드 포함 검색 (isDeleted=false)")
    void searchByTitleContaining() {
        List<Board> boards = boardRepository.findByTitleContainingAndIsDeletedFalse("title");
        assertThat(boards).isNotNull();
    }
}
