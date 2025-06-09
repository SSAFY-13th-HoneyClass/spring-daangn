package com.example.daangn.controller;

import com.example.daangn.domain.location.entity.Location;
import com.example.daangn.domain.location.repository.LocationRepository;
import com.example.daangn.domain.product.dto.ProductRequestDto;
import com.example.daangn.domain.product.dto.ProductResponseDto;
import com.example.daangn.domain.product.entity.Product;
import com.example.daangn.domain.product.service.ProductService;
import com.example.daangn.domain.user.entity.User;
import com.example.daangn.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserService userService;
    private final LocationRepository locationRepository;

    /**
     * 새로운 상품 생성
     */
    @PostMapping("/")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequestDto requestDto) {
        // 사용자 존재 여부 확인
        Optional<User> user = userService.findByUID(requestDto.getUserId());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("존재하지 않는 사용자입니다.");
        }

        // 위치 존재 여부 확인
        Optional<Location> location = locationRepository.findById(requestDto.getLocationId());
        if (location.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("존재하지 않는 위치입니다.");
        }

        // DTO를 엔티티로 변환
        Product product = ProductRequestDto.toEntity(requestDto, location.get(), user.get());

        // 상품 저장
        Product savedProduct = productService.save(product);

        // 생성된 상품 정보를 DTO로 변환하여 반환
        ProductResponseDto responseDto = ProductResponseDto.fromEntity(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 모든 상품 조회
     */
    @GetMapping("/")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<Product> products = productService.findAll();

        // 엔티티 리스트를 DTO 리스트로 변환
        List<ProductResponseDto> responseDtos = products.stream()
                .map(ProductResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    /**
     * 특정 상품 조회
     */
    @GetMapping("/{id}/")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Product product = productService.findByPuid(id);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 상품을 찾을 수 없습니다.");
        }

        ProductResponseDto responseDto = ProductResponseDto.fromEntity(product);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 특정 상품 삭제
     */
    @DeleteMapping("/{id}/")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        Product product = productService.findByPuid(id);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 상품을 찾을 수 없습니다.");
        }

        productService.delete(id);
        return ResponseEntity.ok("상품이 성공적으로 삭제되었습니다.");
    }
}