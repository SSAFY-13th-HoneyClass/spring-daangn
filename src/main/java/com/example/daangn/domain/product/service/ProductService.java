package com.example.daangn.domain.product.service;

import com.example.daangn.domain.product.entity.Product;
import com.example.daangn.domain.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    //모든 상품 조회
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    //product uid로 특정 상품 조회
    public Product findByPuid(Long puid) {
        return productRepository.findByPuid(puid);
    }

    //product title로 상품 리스트 조회
    public List<Product> findAllByTitle(String title) {
        return productRepository.findAllByTitle(title);
    }

    //상품 게시글 등록
    public Product save(Product product) {
        return productRepository.save(product);
    }

    //상품 게시글 수정
    public Product update(Product product) {
        return productRepository.save(product);
    }

    //상품 게시글 삭제
    public void delete(Long puid) {
        productRepository.deleteById(puid);
    }
}
