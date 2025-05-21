package com.ssafy.daangn.comment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.comment.dto.request.CommentRequestDto;
import com.ssafy.daangn.comment.dto.response.CommentResponseDto;
import com.ssafy.daangn.comment.entity.Comment;
import com.ssafy.daangn.comment.repository.CommentRepository;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CommentResponseDto createComment(CommentRequestDto dto) {
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Comment parent = null;
        if (dto.getParentCommentId() != null) {
            parent = commentRepository.findById(dto.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
        }

        Comment comment = Comment.builder()
                .board(board)
                .member(member)
                .parent(parent)
                .content(dto.getContent())
                .build();

        return CommentResponseDto.fromEntity(commentRepository.save(comment));
    }

    public List<CommentResponseDto> getAllComments() {
        return commentRepository.findAll().stream()
                .map(CommentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<CommentResponseDto> getCommentsByBoard(Long boardId) {
        return commentRepository.findByBoard_BoardIdAndIsDeletedFalse(boardId).stream()
                .map(CommentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<CommentResponseDto> getRootCommentsByBoard(Long boardId) {
        return commentRepository.findByBoard_BoardIdAndParentIsNullAndIsDeletedFalse(boardId).stream()
                .map(CommentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<CommentResponseDto> getChildComments(Long parentCommentId) {
        return commentRepository.findByParent_CommentIdAndIsDeletedFalse(parentCommentId).stream()
                .map(CommentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
