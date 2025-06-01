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
                .build();

        return BoardPhotoResponseDto.from(boardPhotoRepository.save(photo));
    }

    public BoardPhotoResponseDto getPhotoById(Long photoId) {
        BoardPhoto photo = boardPhotoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사진을 찾을 수 없습니다."));
        return BoardPhotoResponseDto.from(photo);
    }    

    public List<BoardPhotoResponseDto> getPhotosByBoard(Long boardId) {
        return boardPhotoRepository.findByBoard_BoardIdOrderByCreatedAtAsc(boardId).stream()
                .map(BoardPhotoResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePhoto(Long photoId) {
        boardPhotoRepository.deleteById(photoId);
    }

    @Transactional
    public BoardPhotoResponseDto updatePhoto(Long photoId, BoardPhotoRequestDto dto) {
        BoardPhoto photo = boardPhotoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));

        if (dto.getUrl() != null) {
            photo.setUrl(dto.getUrl());
        }

        return BoardPhotoResponseDto.from(photo);
    }
}
