package com.ssafy.spring_boot.serviceTest;


import com.ssafy.spring_boot.category.domain.Category;
import com.ssafy.spring_boot.category.repository.CategoryRepository;
import com.ssafy.spring_boot.image.domain.Image;
import com.ssafy.spring_boot.image.dto.ImageDTO;
import com.ssafy.spring_boot.image.repository.ImageRepository;
import com.ssafy.spring_boot.product.domain.Product;
import com.ssafy.spring_boot.product.dto.ProductDTO;
import com.ssafy.spring_boot.product.repository.ProductRepository;
import com.ssafy.spring_boot.product.service.ProductServiceImpl;
import com.ssafy.spring_boot.region.domain.Region;
import com.ssafy.spring_boot.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @DisplayName("상품 상세 조회 및 조회수 증가 테스트")
    void getProductDetailTest() {
        // given
        Region region = Region.builder().id(1).name("서울").build();
        Category category = Category.builder().id(1).type("전자기기").build();
        User seller = User.builder()
                .id(1)
                .nickname("판매자")
                .region(region)
                .temperature(36.5)
                .build();

        Product product = Product.builder()
                .id(1L)
                .title("아이폰")
                .thumbnail("thumbnail.jpg")
                .description("새 제품입니다")
                .price(1000000)
                .createdAt(LocalDateTime.now())
                .isReserved(false)
                .isCompleted(false)
                .isNegotiable(true)
                .chatCount(5L)
                .viewCount(10L)
                .favoriteCount(3L)
                .seller(seller)
                .category(category)
                .region(region)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when
        ProductDTO result = productService.getProductDetail(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("아이폰");

        // 조회수 증가 검증
        assertThat(product.getViewCount()).isEqualTo(11L); // 10 -> 11로 증가

        // save 메서드 호출 검증
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 예외 발생 테스트")
    void getProductDetailNotFoundTest() {
        // given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            productService.getProductDetail(99L);
        });
    }

    @Test
    @DisplayName("상품 이미지 목록 조회 테스트")
    void getProductImagesTest() {
        // given
        Product product = Product.builder()
                .id(1L)
                .title("아이폰")
                .build();

        Image image1 = Image.builder()
                .id(1L)
                .product(product)
                .imageUrl("image1.jpg")
                .order(1)
                .build();

        Image image2 = Image.builder()
                .id(2L)
                .product(product)
                .imageUrl("image2.jpg")
                .order(2)
                .build();

        List<Image> images = Arrays.asList(image1, image2);

        when(imageRepository.findAllByProduct_Id(1L)).thenReturn(images);

        // when
        List<ImageDTO> result = productService.getProductImages(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getImageUrl()).isEqualTo("image1.jpg");
        assertThat(result.get(1).getImageUrl()).isEqualTo("image2.jpg");

        verify(imageRepository).findAllByProduct_Id(1L);
    }
}