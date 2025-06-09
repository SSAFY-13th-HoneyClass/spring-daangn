package com.ssafy.daangn_demo.service;

import com.ssafy.daangn_demo.dto.request.ProductRequest;
import com.ssafy.daangn_demo.entity.AreaEntity;
import com.ssafy.daangn_demo.entity.CategoryEntity;
import com.ssafy.daangn_demo.entity.ProductEntity;
import com.ssafy.daangn_demo.entity.UserEntity;
import com.ssafy.daangn_demo.exception.CustomException;
import com.ssafy.daangn_demo.exception.ErrorCode;
import com.ssafy.daangn_demo.repository.AreaRepository;
import com.ssafy.daangn_demo.repository.CategoryRepository;
import com.ssafy.daangn_demo.repository.ProductRepository;
import com.ssafy.daangn_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AreaRepository areaRepository;

    public ProductEntity create(ProductRequest productRequest) {
        UserEntity writer = userRepository.findById(productRequest.getWriterId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        CategoryEntity category = categoryRepository.findById(productRequest.getCategoryId()).orElse(null);
        AreaEntity area = areaRepository.findById(productRequest.getAreaId()).orElse(null);
        ProductEntity productEntity = ProductEntity.from(productRequest, writer, category, area);
        return productRepository.save(productEntity);
    }

    public List<ProductEntity> getByWriter(Long writerId) {
        return productRepository.findAllByWriterId(writerId);
    }

    public List<ProductEntity> getAll() {
        return productRepository.findAll();
    }

    public ProductEntity getById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public void delete(Long productId) {
        productRepository.deleteById(productId);
    }

}
