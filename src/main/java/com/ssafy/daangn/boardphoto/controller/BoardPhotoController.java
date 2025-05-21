package com.ssafy.daangn.boardphoto.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.daangn.boardphoto.dto.request.BoardPhotoRequestDto;
import com.ssafy.daangn.boardphoto.dto.response.BoardPhotoResponseDto;
import com.ssafy.daangn.boardphoto.service.BoardPhotoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class BoardPhotoController {

    private final BoardPhotoService boardPhotoService;

    @PostMapping
    public ResponseEntity<BoardPhotoResponseDto> addPhoto(@RequestBody BoardPhotoRequestDto dto) {
        return ResponseEntity.ok(boardPhotoService.addPhoto(dto));
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<BoardPhotoResponseDto>> getPhotos(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardPhotoService.getPhotosByBoard(boardId));
    }
}
