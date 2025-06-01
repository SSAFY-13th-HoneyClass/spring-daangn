package com.ssafy.daangn.favorite.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.daangn.board.dto.response.BoardResponseDto;
import com.ssafy.daangn.favorite.dto.request.FavoriteRequestDto;
import com.ssafy.daangn.favorite.dto.response.FavoriteMemberResponseDto;
import com.ssafy.daangn.favorite.service.FavoriteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/favorite")
@RequiredArgsConstructor
@Tag(name = "Favorite", description = "게시글 좋아요 관련 API")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "좋아요 토글", description = "좋아요가 없으면 추가하고, 있으면 삭제합니다.")
    @PostMapping
    public ResponseEntity<Void> toggleFavorite(@RequestBody FavoriteRequestDto dto) {
        favoriteService.toggle(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원의 게시글 좋아요 여부 확인", description = "해당 회원이 게시글을 좋아요했는지 여부를 반환합니다.")
    @GetMapping("/status")
    public ResponseEntity<Boolean> isFavorited(@RequestParam Long memberId, @RequestParam Long boardId) {
        return ResponseEntity.ok(favoriteService.isFavorited(memberId, boardId));
    }

    @Operation(summary = "회원이 좋아요한 게시글 목록 조회", description = "해당 회원이 좋아요한 게시글 목록을 반환합니다.")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<BoardResponseDto>> getFavoriteBoards(@PathVariable Long memberId) {
        return ResponseEntity.ok(favoriteService.getFavoriteBoardsByMember(memberId));
    }

    @Operation(summary = "게시글 좋아요한 회원 목록 조회", description = "게시글을 좋아요한 회원들의 ID, 이름, 프로필 URL을 반환합니다.")
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<FavoriteMemberResponseDto>> getMembersWhoLikedBoard(@PathVariable Long boardId) {
        List<FavoriteMemberResponseDto> members = favoriteService.getMembersWhoLikedBoard(boardId);
        return ResponseEntity.ok(members);
    }

    @Operation(summary = "게시글 좋아요 개수 조회", description = "특정 게시글의 좋아요 수를 반환합니다.")
    @GetMapping("/board/{boardId}/count")
    public ResponseEntity<Long> getFavoriteCount(@PathVariable Long boardId) {
        return ResponseEntity.ok(favoriteService.getFavoriteCount(boardId));
    }

}
