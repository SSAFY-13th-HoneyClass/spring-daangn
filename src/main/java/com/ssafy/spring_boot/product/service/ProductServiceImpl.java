package com.ssafy.spring_boot.product.service;

import com.ssafy.spring_boot.category.domain.Category;
import com.ssafy.spring_boot.category.dto.CategoryDTO;
import com.ssafy.spring_boot.category.repository.CategoryRepository;
import com.ssafy.spring_boot.comment.domain.Comment;
import com.ssafy.spring_boot.comment.dto.CommentDTO;
import com.ssafy.spring_boot.comment.repository.CommentRepository;
import com.ssafy.spring_boot.image.dto.ImageDTO;
import com.ssafy.spring_boot.image.repository.ImageRepository;
import com.ssafy.spring_boot.product.domain.Product;
import com.ssafy.spring_boot.product.dto.ProductCreateRequestDTO;
import com.ssafy.spring_boot.product.dto.ProductDTO;
import com.ssafy.spring_boot.product.repository.ProductRepository;
import com.ssafy.spring_boot.region.domain.Region;
import com.ssafy.spring_boot.region.repository.RegionRepository;
import com.ssafy.spring_boot.user.domain.User;
import com.ssafy.spring_boot.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;

    @Override
    @Transactional
    public ProductDTO createProduct(ProductCreateRequestDTO requestDTO) {
        // 연관 엔티티 조회
        User seller = userRepository.findById(requestDTO.getSellerId().longValue())
                .orElseThrow(() -> new EntityNotFoundException("판매자를 찾을 수 없습니다. ID: " + requestDTO.getSellerId()));

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + requestDTO.getCategoryId()));

        Region region = regionRepository.findById(requestDTO.getRegionId())
                .orElseThrow(() -> new EntityNotFoundException("지역을 찾을 수 없습니다. ID: " + requestDTO.getRegionId()));

        // DTO -> Entity 변환 후 저장
        Product product = ProductCreateRequestDTO.toEntity(requestDTO, seller, category, region);
        Product savedProduct = productRepository.save(product);

        return ProductDTO.from(savedProduct);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAllWithDetails();
        return products.stream()
                .map(ProductDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. ID: " + productId));

        productRepository.delete(product);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long productId, ProductCreateRequestDTO requestDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. ID: " + productId));

        // 연관 엔티티 조회 (변경된 경우만)
        if (requestDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + requestDTO.getCategoryId()));
            product.setCategory(category);
        }

        if (requestDTO.getRegionId() != null) {
            Region region = regionRepository.findById(requestDTO.getRegionId())
                    .orElseThrow(() -> new EntityNotFoundException("지역을 찾을 수 없습니다. ID: " + requestDTO.getRegionId()));
            product.setRegion(region);
        }

        // 기본 필드 업데이트
        if (requestDTO.getTitle() != null) {
            product.setTitle(requestDTO.getTitle());
        }
        if (requestDTO.getThumbnail() != null) {
            product.setThumbnail(requestDTO.getThumbnail());
        }
        if (requestDTO.getDescription() != null) {
            product.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getPrice() != null) {
            product.setPrice(requestDTO.getPrice());
        }
        if (requestDTO.getIsNegotiable() != null) {
            product.setIsNegotiable(requestDTO.getIsNegotiable());
        }

        Product updatedProduct = productRepository.save(product);
        return ProductDTO.from(updatedProduct);
    }

    @Override
    @Transactional
    public ProductDTO getProductDetail(Long productId) {
        Product product = productRepository.findByIdWithAll(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. ID: " + productId));

        // 조회수 증가
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);

        return ProductDTO.from(product);
    }

    @Override
    public List<ImageDTO> getProductImages(Long productId) {
        return imageRepository.findByProductIdOrderByOrderAsc(productId).stream()
                .map(ImageDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getProductCategory(Long productId) {
        Product product = productRepository.findByIdWithCategory(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. ID: " + productId));

        return CategoryDTO.from(product.getCategory());
    }

    @Override
    public List<CommentDTO> getProductComments(Long productId) {
        return commentRepository.findAllByProductIdOrderByCreateAtAsc(productId)
                .stream()
                .map(CommentDTO::from)
                .collect(Collectors.toList());
    }
}