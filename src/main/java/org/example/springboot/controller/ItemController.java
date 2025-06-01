package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.ItemDto;
import org.example.springboot.exception.ErrorResponse;
import org.example.springboot.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "Item Management", description = "당근마켓 아이템 관리 API")
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "새로운 아이템 생성", description = "새로운 당근마켓 아이템을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "아이템 생성 성공",
                    content = @Content(schema = @Schema(implementation = ItemDto.DetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ItemDto.DetailResponse> createItem(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "생성할 아이템 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ItemDto.CreateRequest.class))
            )
            @RequestBody ItemDto.CreateRequest request) {
        log.info("Creating new item with request: {}", request);
        
        ItemDto.DetailResponse response = itemService.createItem(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "모든 아이템 조회", description = "등록된 모든 아이템 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아이템 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ItemDto.ListResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<ItemDto.ListResponse>> getAllItems() {
        log.info("Retrieving all items");
        
        List<ItemDto.ListResponse> responses = itemService.getAllItems();
        
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "특정 아이템 조회", description = "ID로 특정 아이템의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아이템 조회 성공",
                    content = @Content(schema = @Schema(implementation = ItemDto.DetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ItemDto.DetailResponse> getItemById(
            @Parameter(description = "조회할 아이템의 ID", required = true, example = "1")
            @PathVariable("id") Long itemId) {
        log.info("Retrieving item with id: {}", itemId);
        
        ItemDto.DetailResponse response = itemService.getItemById(itemId);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 아이템 삭제", description = "ID로 특정 아이템을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "아이템 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @Parameter(description = "삭제할 아이템의 ID", required = true, example = "1")
            @PathVariable("id") Long itemId) {
        log.info("Deleting item with id: {}", itemId);
        
        itemService.deleteItem(itemId);
        
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "특정 아이템 업데이트", description = "ID로 특정 아이템의 정보를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아이템 업데이트 성공",
                    content = @Content(schema = @Schema(implementation = ItemDto.DetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ItemDto.DetailResponse> updateItem(
            @Parameter(description = "업데이트할 아이템의 ID", required = true, example = "1")
            @PathVariable("id") Long itemId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "업데이트할 아이템 정보 (모든 필드 선택사항)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ItemDto.UpdateRequest.class))
            )
            @RequestBody ItemDto.UpdateRequest request) {
        log.info("Updating item with id: {} and request: {}", itemId, request);
        
        ItemDto.DetailResponse response = itemService.updateItem(itemId, request);
        
        return ResponseEntity.ok(response);
    }
} 