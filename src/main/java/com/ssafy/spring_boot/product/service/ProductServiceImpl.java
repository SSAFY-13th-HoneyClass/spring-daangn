package com.ssafy.spring_boot.product.service;

import com.ssafy.spring_boot.category.dto.CategoryDTO;
import com.ssafy.spring_boot.category.repository.CategoryRepository;
import com.ssafy.spring_boot.comment.domain.Comment;
import com.ssafy.spring_boot.comment.dto.CommentDTO;
import com.ssafy.spring_boot.comment.repository.CommentRepository;
import com.ssafy.spring_boot.image.dto.ImageDTO;
import com.ssafy.spring_boot.image.repository.ImageRepository;
import com.ssafy.spring_boot.product.domain.Product;
import com.ssafy.spring_boot.product.dto.ProductDTO;
import com.ssafy.spring_boot.product.repository.ProductRepository;
import com.ssafy.spring_boot.product.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;

    @Override
    public ProductDTO getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. ID: " + productId));

        // 조회수 증가
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);

        return ProductDTO.from(product);
    }

    @Override
    public List<ImageDTO> getProductImages(Long productId) {
        return imageRepository.findAllByProduct_Id(productId).stream()
                .map(ImageDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getProductCategory(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. ID: " + productId));

        return CategoryDTO.from(product.getCategory());
    }

    @Override
    public List<CommentDTO> getProductComments(Long productId) {
        return commentRepository.findAllByProduct_Id(productId)
                .stream()
                .map(CommentDTO::from)
                .collect(Collectors.toList());
    }

    // 추가 메소드: 댓글을 DTO로 변환
    public List<CommentDTO> getProductCommentsDTO(Long productId) {
        return commentRepository.findAllByProduct_Id(productId).stream()
                .map(CommentDTO::from)
                .collect(Collectors.toList());
    }
}