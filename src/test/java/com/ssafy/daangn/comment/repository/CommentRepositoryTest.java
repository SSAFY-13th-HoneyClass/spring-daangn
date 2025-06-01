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

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("게시글 ID로 댓글 목록을 조회한다")
    void findByBoardId() {
        Member member = memberRepository.save(Member.builder()
                .membername("user")
                .email("user@test.com")
                .password("pass")
                .build());

        Board board = boardRepository.save(Board.builder()
                .member(member)
                .title("title")
                .content("content")
                .build());

        Comment comment = commentRepository.save(Comment.builder()
                .member(member)
                .board(board)
                .content("댓글입니다.")
                .build());

        List<Comment> result = commentRepository.findByBoard_BoardIdAndIsDeletedFalse(board.getBoardId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("댓글입니다.");
    }

    @Test
    @DisplayName("루트 댓글만 조회한다")
    void findRootComments() {
        Member member = memberRepository.save(Member.builder().membername("root").email("r@r.com").password("1234").build());
        Board board = boardRepository.save(Board.builder().member(member).title("t").content("c").build());

        Comment root = commentRepository.save(Comment.builder().member(member).board(board).content("root").build());
        Comment child = commentRepository.save(Comment.builder().member(member).board(board).parent(root).content("child").build());

        List<Comment> result = commentRepository.findByBoard_BoardIdAndParentIsNullAndIsDeletedFalse(board.getBoardId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("root");
    }
}