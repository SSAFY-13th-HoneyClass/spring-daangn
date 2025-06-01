package com.ssafy.daangn.boardphoto.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
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
import com.ssafy.daangn.boardphoto.dto.request.BoardPhotoRequestDto;
import com.ssafy.daangn.boardphoto.dto.response.BoardPhotoResponseDto;
import com.ssafy.daangn.boardphoto.entity.BoardPhoto;
import com.ssafy.daangn.boardphoto.repository.BoardPhotoRepository;

class BoardPhotoServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardPhotoRepository boardPhotoRepository;

    @InjectMocks
    private BoardPhotoService boardPhotoService;

    private Board board;
    private BoardPhoto photo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        board = Board.builder()
                .boardId(1L)
                .title("제목")
                .content("내용")
                .isDeleted(false)
                .build();

        photo = BoardPhoto.builder()
                .photoId(1L)
                .board(board)
                .url("https://img.com/1.jpg")
                .build();
    }

    @Test
    @DisplayName("사진을 등록할 수 있다")
    void addPhoto() {
        BoardPhotoRequestDto dto = new BoardPhotoRequestDto();
        dto.setBoardId(1L);
        dto.setUrl("https://img.com/1.jpg");

        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        given(boardPhotoRepository.save(any())).willReturn(photo);

        BoardPhotoResponseDto result = boardPhotoService.addPhoto(dto);

        assertThat(result.getPhotoId()).isEqualTo(1L);
        assertThat(result.getUrl()).isEqualTo("https://img.com/1.jpg");
    }

    @Test
    @DisplayName("게시글 ID로 사진을 조회할 수 있다")
    void getPhotosByBoard() {
        given(boardPhotoRepository.findByBoard_BoardIdOrderByCreatedAtAsc(1L))
                .willReturn(Arrays.asList(photo));

        List<BoardPhotoResponseDto> result = boardPhotoService.getPhotosByBoard(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUrl()).isEqualTo("https://img.com/1.jpg");
    }

    @Test
    @DisplayName("사진 단건 조회가 가능하다")
    void getPhotoById() {
        given(boardPhotoRepository.findById(1L)).willReturn(Optional.of(photo));

        BoardPhotoResponseDto result = boardPhotoService.getPhotoById(1L);

        assertThat(result.getPhotoId()).isEqualTo(1L);
        assertThat(result.getUrl()).isEqualTo("https://img.com/1.jpg");
    }

    @Test
    @DisplayName("사진을 삭제할 수 있다")
    void deletePhoto() {
        boardPhotoService.deletePhoto(1L);
        verify(boardPhotoRepository, times(1)).deleteById(1L);
    }
}