package com.ssafy.daangn.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.comment.dto.request.CommentRequestDto;
import com.ssafy.daangn.comment.dto.response.CommentResponseDto;
import com.ssafy.daangn.comment.entity.Comment;
import com.ssafy.daangn.comment.repository.CommentRepository;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member member;
    private Board board;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        member = Member.builder().memberId(1L).membername("user").email("user@test.com").password("1234").build();
        board = Board.builder().boardId(1L).member(member).title("t").content("c").build();
    }

    @Test
    @DisplayName("댓글을 생성한다")
    void createComment() {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setBoardId(1L);
        dto.setMemberId(1L);
        dto.setContent("테스트 댓글");

        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(commentRepository.save(org.mockito.ArgumentMatchers.any(Comment.class)))
            .willAnswer(inv -> inv.getArgument(0));

        CommentResponseDto result = commentService.createComment(dto);

        assertThat(result.getBoardId()).isEqualTo(1L);
        assertThat(result.getMemberId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo("테스트 댓글");
    }

    @Test
    @DisplayName("댓글 생성 시 게시글이 없으면 예외 발생")
    void createComment_boardNotFound() {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setBoardId(99L);
        dto.setMemberId(1L);
        dto.setContent("없는 게시글");

        given(boardRepository.findById(99L)).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> commentService.createComment(dto));
    }

    @Test
    @DisplayName("게시글 ID로 댓글 목록을 가져온다")
    void getCommentsByBoard() {
        Comment comment = Comment.builder().commentId(1L).board(board).member(member).content("댓글1").build();
        given(commentRepository.findByBoard_BoardIdAndIsDeletedFalse(1L)).willReturn(List.of(comment));

        List<CommentResponseDto> result = commentService.getCommentsByBoard(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("댓글1");
    }
}
