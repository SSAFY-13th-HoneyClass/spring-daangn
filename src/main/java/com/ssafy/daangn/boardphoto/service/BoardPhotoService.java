package com.ssafy.daangn.boardphoto.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.boardphoto.dto.request.BoardPhotoRequestDto;
import com.ssafy.daangn.boardphoto.dto.response.BoardPhotoResponseDto;
import com.ssafy.daangn.boardphoto.entity.BoardPhoto;
import com.ssafy.daangn.boardphoto.repository.BoardPhotoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardPhotoService {

    private final BoardPhotoRepository boardPhotoRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public BoardPhotoResponseDto addPhoto(BoardPhotoRequestDto dto) {
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        BoardPhoto photo = BoardPhoto.builder()
                .board(board)
                .url(dto.getUrl())
                .photoOrder(dto.getPhotoOrder())
                .build();

        return BoardPhotoResponseDto.from(boardPhotoRepository.save(photo));
    }

    public List<BoardPhotoResponseDto> getPhotosByBoard(Long boardId) {
        return boardPhotoRepository.findByBoard_BoardIdOrderByPhotoOrder(boardId)
                .stream()
                .map(BoardPhotoResponseDto::from)
                .collect(Collectors.toList());
    }
}
