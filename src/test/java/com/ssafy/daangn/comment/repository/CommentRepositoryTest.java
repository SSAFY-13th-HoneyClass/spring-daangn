package com.ssafy.daangn.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.comment.entity.Comment;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("대댓글 포함 댓글 저장 및 부모-자식 연관관계 테스트")
    void saveAndLoadCommentsWithParent() {
        Member member = memberRepository.save(Member.builder()
                .membername("user")
                .email("user@x.com")
                .password("123")
                .build());

        Board board = boardRepository.save(Board.builder()
                .member(member)
                .title("board")
                .content("content")
                .build());

        Comment parent = commentRepository.save(Comment.builder()
                .member(member)
                .board(board)
                .content("부모 댓글")
                .build());

        Comment child = commentRepository.save(Comment.builder()
                .member(member)
                .board(board)
                .parent(parent)
                .content("자식 댓글")
                .build());

        em.clear(); // Lazy loading 확인
        Comment foundChild = commentRepository.findById(child.getCommentId()).orElseThrow();

        assertThat(foundChild.getParent().getContent()).isEqualTo("부모 댓글");
    }

    @Test
    @DisplayName("게시글 기반 루트 댓글 조회 테스트")
    void findRootCommentsByBoard() {
        List<Comment> roots = commentRepository.findByBoard_BoardIdAndParentIsNullAndIsDeletedFalse(1L);
        assertThat(roots).isNotNull();
    }

    @Test
    @DisplayName("N+1 테스트용 - 댓글 조회 시 member 또는 board에 대한 join 필요성 확인")
    void checkNPlusOneIssue() {
        List<Comment> comments = commentRepository.findByBoard_BoardIdAndIsDeletedFalse(1L);
        for (Comment c : comments) {
            // Lazy로 설정한 member, board 접근 시 N+1 가능성 확인
            System.out.println(c.getMember().getEmail());
        }
    }
}
