package com.ssafy.daangn_demo.service;

import com.ssafy.daangn_demo.entity.ProductEntity;
import com.ssafy.daangn_demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void create(ProductEntity productEntity) {
        productRepository.save(productEntity);
    }

    public List<ProductEntity> getByWriter(Long writerId) {
        return productRepository.findAllByWriterId(writerId);
    }

}
