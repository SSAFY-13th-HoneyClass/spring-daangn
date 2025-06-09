
// SaleController.java
package com.ssafy.daangn.controller;

import com.ssafy.daangn.domain.Sale;
import com.ssafy.daangn.dto.SaleDetailRequestDto;
import com.ssafy.daangn.dto.SaleDetailResponseDto;
import com.ssafy.daangn.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/sale")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sale", description = "중고거래 게시글 관리 API")
public class SaleController {

    private final SaleService saleService;

    @Operation(summary = "판매글 등록", description = "새로운 판매글을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createSale(
            @ModelAttribute SaleDetailRequestDto dto
//            ,@Parameter(description = "썸네일 이미지") @RequestParam("thumbnail") MultipartFile thumbnail,
//            @Parameter(description = "상세 이미지들") @RequestParam("images") List<MultipartFile> images
            ) {
        log.info("dto : {}", dto);
        try {
            saleService.save(dto, dto.getThumbnail(), dto.getImages());
            return ResponseEntity.ok("판매글이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            log.error("판매글 등록 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body("판매글 등록에 실패했습니다.");
        }
    }

    @Operation(summary = "판매글 상세 조회", description = "판매글 번호로 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "판매글을 찾을 수 없음")
    })
    @GetMapping("/{no}")
    public ResponseEntity<SaleDetailResponseDto> getSale(
            @Parameter(description = "판매글 번호") @PathVariable Long no) {

        try {
            SaleDetailResponseDto sale = saleService.findByNo(no);
            return ResponseEntity.ok(sale);
        } catch (Exception e) {
            log.error("판매글 조회 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "판매글 수정", description = "기존 판매글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "판매글을 찾을 수 없음")
    })
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateSale(
            @ModelAttribute SaleDetailRequestDto dto,
            @Parameter(description = "썸네일 이미지") @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @Parameter(description = "상세 이미지들") @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        try {
            saleService.update(dto, thumbnail, images);
            return ResponseEntity.ok("판매글이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            log.error("판매글 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body("판매글 수정에 실패했습니다.");
        }
    }

    @Operation(summary = "판매글 삭제", description = "판매글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "판매글을 찾을 수 없음")
    })
    @DeleteMapping("/{no}")
    public ResponseEntity<?> deleteSale(
            @Parameter(description = "판매글 번호") @PathVariable Long no) {

        try {
            saleService.delete(no);
            return ResponseEntity.ok("판매글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("판매글 삭제 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body("판매글 삭제에 실패했습니다.");
        }
    }

    @Operation(summary = "사용자별 판매글 조회", description = "특정 사용자의 판매글 목록을 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Sale>> getSalesByUser(
            @Parameter(description = "사용자 아이디") @PathVariable String userId) {

        List<Sale> sales = saleService.findByUser_IdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(sales);
    }

    @Operation(summary = "키워드 검색", description = "제목이나 내용에서 키워드를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<Sale>> searchSales(
            @Parameter(description = "검색 키워드") @RequestParam String keyword) {

        List<Sale> sales = saleService.findByTitleContainingOrContentContainingOrderByCreatedAtDesc(keyword, keyword);
        return ResponseEntity.ok(sales);
    }

    @Operation(summary = "위치 기반 검색", description = "특정 위치 주변의 판매글을 검색합니다.")
    @GetMapping("/nearby")
    public ResponseEntity<List<Sale> > getNearbySales(
            @Parameter(description = "위도") @RequestParam double latitude,
            @Parameter(description = "경도") @RequestParam double longitude,
            @Parameter(description = "검색 반경(km)", example = "3.0") @RequestParam(defaultValue = "3.0") double distance) {

        List<Sale> sales = saleService.findByLocationNear(latitude, longitude, distance);
        return ResponseEntity.ok(sales);
    }
}
