package com.ssafy.daangn_demo.controller;

import com.ssafy.daangn_demo.dto.request.ProductRequest;
import com.ssafy.daangn_demo.dto.response.ProductResponse;
import com.ssafy.daangn_demo.entity.ProductEntity;
import com.ssafy.daangn_demo.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "상품 판매 등록 API")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest productRequest) {
        ProductEntity productEntity = productService.create(productRequest);
        return ResponseEntity.ok(ProductResponse.from(productEntity));
    }

    @Operation(summary = "상품 판매 목록 전체 조회 API")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        List<ProductEntity> productEntities = productService.getAll();
        List<ProductResponse> productResponses = productEntities.stream().map(ProductResponse::from).toList();
        return ResponseEntity.ok(productResponses);
    }

    @Operation(summary = "상품 단건 조회 API")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long productId) {
        ProductEntity productEntity = productService.getById(productId);
        return ResponseEntity.ok(ProductResponse.from(productEntity));
    }

    @Operation(summary = "상품 삭제 API")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Objects> delete(@PathVariable Long productId) {
        productService.delete(productId);
        return ResponseEntity.ok().build();
    }
}
