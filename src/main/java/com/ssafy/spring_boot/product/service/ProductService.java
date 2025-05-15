package com.ssafy.spring_boot.product.service;

import com.ssafy.spring_boot.category.dto.CategoryDTO;
import com.ssafy.spring_boot.image.dto.ImageDTO;
import com.ssafy.spring_boot.product.dto.ProductDTO;

import java.util.List;

public interface ProductService {

    /**
     * 특정 상품의 상세 정보를 조회
     * - 상품에 연결된 사진, 카테고리, 지역, 판매자 등도 함께 조회해야 함
     * - fetch join 또는 EntityGraph 등을 활용하여 N+1 문제 방지
     *
     * @param productId 조회할 상품 ID
     * @return 상품 전체 정보가 담긴 Product 엔티티 또는 DTO
     */
    ProductDTO getProductDetail(Long productId);

    /**
     * 상품 ID를 기준으로 해당 상품에 등록된 이미지 목록을 조회
     * - 1:N 관계이므로 fetch join 또는 별도 ImageRepository 사용 가능
     *
     * @param productId 이미지가 포함된 상품 ID
     * @return 상품 이미지 리스트
     */
    List<ImageDTO> getProductImages(Long productId);

    /**
     * 특정 상품의 카테고리 정보를 조회
     * - 단순 참조가 아니라 명확히 분리된 메서드로 제공
     *
     * @param productId 상품 ID
     * @return 상품의 카테고리 엔티티
     */
    CategoryDTO getProductCategory(Long productId);

    /**
     * 해당 상품에 달린 모든 댓글을 조회
     * - 댓글 목록은 최신순 또는 등록순 정렬 가능
     *
     * @param productId 댓글을 가져올 상품 ID
     * @return 댓글 목록
     */
    List<Comment> getProductComments(Long productId);
}
