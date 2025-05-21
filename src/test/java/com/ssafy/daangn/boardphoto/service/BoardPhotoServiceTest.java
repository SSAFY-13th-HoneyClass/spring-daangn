package com.ssafy.daangn.boardphoto.service;

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
import com.ssafy.daangn.boardphoto.dto.request.BoardPhotoRequestDto;
import com.ssafy.daangn.boardphoto.dto.response.BoardPhotoResponseDto;
import com.ssafy.daangn.boardphoto.entity.BoardPhoto;
import com.ssafy.daangn.boardphoto.repository.BoardPhotoRepository;

@ExtendWith(MockitoExtension.class)
class BoardPhotoServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardPhotoRepository boardPhotoRepository;

    @InjectMocks
    private BoardPhotoService boardPhotoService;

    private Board board;

    @BeforeEach
    void setup() {
        board = Board.builder()
                .boardId(1L)
                .title("title")
                .content("content")
                .build();
    }

    @Test
    void addPhoto_shouldSaveAndReturnDto() {
        BoardPhotoRequestDto dto = new BoardPhotoRequestDto();
        dto.setBoardId(1L);
        dto.setUrl("image.jpg");
        dto.setPhotoOrder(1);

        BoardPhoto photo = BoardPhoto.builder()
                .photoId(10L)
                .board(board)
                .url("image.jpg")
                .photoOrder(1)
                .build();

        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(boardPhotoRepository.save(any(BoardPhoto.class))).thenReturn(photo);

        BoardPhotoResponseDto response = boardPhotoService.addPhoto(dto);

        assertThat(response.getUrl()).isEqualTo("image.jpg");
        verify(boardPhotoRepository).save(any(BoardPhoto.class));
    }
}
