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

@DataJpaTest
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("삭제되지 않은 게시글 조회")
    void findByIsDeletedFalseTest() {
        Member member = memberRepository.save(Member.builder()
                .membername("사용자")
                .email("test@example.com")
                .password("pw")
                .build());

        Board activeBoard = boardRepository.save(Board.builder()
                .title("게시글1")
                .content("내용")
                .member(member)
                .isDeleted(false)
                .build());

        Board deletedBoard = boardRepository.save(Board.builder()
                .title("삭제된 게시글")
                .content("삭제됨")
                .member(member)
                .isDeleted(true)
                .build());

        List<Board> result = boardRepository.findByIsDeletedFalse();
        assertThat(result).contains(activeBoard);
        assertThat(result).doesNotContain(deletedBoard);
    }

    @Test
    @DisplayName("삭제된 게시글 조회")
    void findByIsDeletedTrueTest() {
        Member member = memberRepository.save(Member.builder()
                .membername("사용자")
                .email("del@example.com")
                .password("pw")
                .build());

        boardRepository.save(Board.builder()
                .title("보이는 게시글")
                .content("보임")
                .member(member)
                .isDeleted(false)
                .build());

        Board deletedBoard = boardRepository.save(Board.builder()
                .title("삭제된")
                .content("숨김")
                .member(member)
                .isDeleted(true)
                .build());

        List<Board> result = boardRepository.findByIsDeletedTrue();
        assertThat(result).hasSize(1).contains(deletedBoard);
    }

    @Test
    @DisplayName("회원의 게시글 조회 (삭제되지 않은)")
    void findByMemberIdAndIsDeletedFalseTest() {
        Member member = memberRepository.save(Member.builder()
                .membername("홍길동")
                .email("hong@domain.com")
                .password("pw")
                .build());

        Board board1 = boardRepository.save(Board.builder()
                .title("제목1")
                .content("내용1")
                .member(member)
                .isDeleted(false)
                .build());

        Board board2 = boardRepository.save(Board.builder()
                .title("제목2")
                .content("내용2")
                .member(member)
                .isDeleted(true) // 삭제된 게시글은 포함되지 않아야 함
                .build());

        List<Board> result = boardRepository.findByMember_MemberIdAndIsDeletedFalse(member.getMemberId());

        assertThat(result).contains(board1);
        assertThat(result).doesNotContain(board2);
    }

    @Test
    @DisplayName("제목 키워드 포함 게시글 검색")
    void findByTitleContainingTest() {
        Member member = memberRepository.save(Member.builder()
                .membername("검색자")
                .email("search@x.com")
                .password("pw")
                .build());

        Board board1 = boardRepository.save(Board.builder()
                .title("테스트 게시글")
                .content("내용")
                .member(member)
                .isDeleted(false)
                .build());

        boardRepository.save(Board.builder()
                .title("삭제된 테스트")
                .content("내용")
                .member(member)
                .isDeleted(true) // 삭제된 게시글은 포함되지 않아야 함
                .build());

        List<Board> results = boardRepository.findByTitleContainingAndIsDeletedFalse("테스트");

        assertThat(results).hasSize(1).contains(board1);
    }
}
