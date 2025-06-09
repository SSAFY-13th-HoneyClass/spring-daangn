package com.ssafy.daangn.boardphoto.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.daangn.boardphoto.dto.request.BoardPhotoRequestDto;
import com.ssafy.daangn.boardphoto.dto.response.BoardPhotoResponseDto;
import com.ssafy.daangn.boardphoto.service.BoardPhotoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/board-photo")
@RequiredArgsConstructor
@Tag(name = "BoardPhoto", description = "게시글 사진 관련 API")
public class BoardPhotoController {

    private final BoardPhotoService boardPhotoService;

    @Operation(summary = "게시글 사진 등록", description = "특정 게시글에 새로운 사진을 등록합니다.")
    @PostMapping
    public ResponseEntity<BoardPhotoResponseDto> addPhoto(@RequestBody BoardPhotoRequestDto dto) {
        return ResponseEntity.ok(boardPhotoService.addPhoto(dto));
    }

    @Operation(summary = "사진 단건 조회", description = "photoId로 특정 게시글 사진 정보를 조회합니다.")
    @GetMapping("/{photoId}")
    public ResponseEntity<BoardPhotoResponseDto> getPhotoById(@PathVariable Long photoId) {
        BoardPhotoResponseDto photo = boardPhotoService.getPhotoById(photoId);
        return ResponseEntity.ok(photo);
    }

    @Operation(summary = "게시글 사진 목록 조회", description = "특정 게시글에 등록된 사진들을 조회합니다.")
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<BoardPhotoResponseDto>> getPhotosByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardPhotoService.getPhotosByBoard(boardId));
    }

    @Operation(summary = "게시글 사진 삭제", description = "특정 게시글 사진 ID를 삭제합니다.")
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long photoId) {
        boardPhotoService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }
}
