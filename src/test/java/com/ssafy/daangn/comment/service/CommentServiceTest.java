package com.ssafy.daangn.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.comment.dto.request.CommentRequestDto;
import com.ssafy.daangn.comment.dto.response.CommentResponseDto;
import com.ssafy.daangn.comment.entity.Comment;
import com.ssafy.daangn.comment.repository.CommentRepository;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
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
        member = Member.builder().memberId(1L).build();
        board = Board.builder().boardId(1L).member(member).build();
    }

    @Test
    void createRootCommentTest() {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setMemberId(1L);
        dto.setBoardId(1L);
        dto.setContent("hello");

        Comment saved = Comment.builder()
                .commentId(1L)
                .member(member)
                .board(board)
                .content("hello")
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        CommentResponseDto result = commentService.createComment(dto);

        assertThat(result.getContent()).isEqualTo("hello");
        verify(commentRepository).save(any(Comment.class));
    }
}
