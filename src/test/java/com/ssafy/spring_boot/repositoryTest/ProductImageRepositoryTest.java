package com.ssafy.spring_boot.repositoryTest;

import com.ssafy.spring_boot.category.domain.Category;
import com.ssafy.spring_boot.category.repository.CategoryRepository;
import com.ssafy.spring_boot.image.domain.Image;
import com.ssafy.spring_boot.image.repository.ImageRepository;
import com.ssafy.spring_boot.product.domain.Product;
import com.ssafy.spring_boot.product.repository.ProductRepository;
import com.ssafy.spring_boot.region.domain.Region;
import com.ssafy.spring_boot.region.repository.RegionRepository;
import com.ssafy.spring_boot.user.domain.User;
import com.ssafy.spring_boot.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductImageRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ImageRepository imageRepository;

    @PersistenceContext
    private EntityManager em;

    private Region region;
    private Category category;
    private User seller;
    private List<Product> products = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        region = regionRepository.save(Region.builder().name("서울").build());
        category = categoryRepository.save(Category.builder().type("전자기기").build());
        seller = userRepository.save(
                User.builder()
                        .region(region)
                        .nickname("판매자")
                        .temperature(36.5)
                        .build()
        );

        // 5개의 상품 생성
        String[] productNames = {"아이폰", "갤럭시", "맥북", "아이패드", "애플워치"};
        for (int p = 0; p < 5; p++) {
            Product product = productRepository.save(Product.builder()
                    .title(productNames[p])
                    .thumbnail("thumbnail_" + p + ".jpg")
                    .category(category)
                    .region(region)
                    .seller(seller)
                    .price(1000000 - p * 100000)
                    .description(productNames[p] + " 상품입니다")
                    .build());

            products.add(product);

            // 각 상품마다 5개의 이미지 저장
            for (int i = 0; i < 5; i++) {
                imageRepository.save(Image.builder()
                        .product(product)
                        .imageUrl(productNames[p] + "_image" + i + ".jpg")
                        .order(i)
                        .build());
            }
        }

        // 영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        System.out.println("======= 테스트 데이터 설정 완료: 상품 5개, 각 상품당 이미지 5개 =======");
    }

    @Test
    @DisplayName("N+1 문제 발생 테스트: 5개 상품 조회 후 이미지 접근")
    void testNPlus1Problem() {
        // when - 1개의 쿼리 실행 (상품 조회)
        System.out.println("\n\n======= 상품 목록 조회 시작 (1개의 쿼리 예상) =======");
        List<Product> allProducts = productRepository.findAll();
        System.out.println("======= 상품 목록 조회 완료: " + allProducts.size() + "개 상품 =======\n");

        // then
        assertThat(allProducts).hasSize(5);

        // N개의 쿼리 추가 실행 (각 상품의 이미지 조회 - N+1 문제)
        System.out.println("\n======= 이미지 접근 시작 (N+1 문제: 5개의 추가 쿼리 예상) =======");
        int productCount = 0;
        for (Product p : allProducts) {
            productCount++;
            System.out.println("\n----- 상품 " + productCount + ": " + p.getTitle() + " 의 이미지 조회 시작 -----");
            // 이미지 접근 시 추가 쿼리 발생 (N+1 문제)
            List<Image> images = imageRepository.findAllByProduct_Id(p.getId());
            assertThat(images).hasSize(5);

            // 이미지 URL 출력 (실제 데이터 접근)
            System.out.println(p.getTitle() + "의 이미지 " + images.size() + "개:");
            images.forEach(image -> System.out.println("  - " + image.getImageUrl()));
            System.out.println("----- 상품 " + productCount + "의 이미지 조회 완료 -----");
        }
        System.out.println("\n======= 이미지 접근 완료 (총 1 + 5 = 6개 쿼리 실행됨) =======");
    }

    @Test
    @DisplayName("N+1 문제 해결 테스트: JOIN FETCH 사용")
    void testSolveNPlus1WithJoinFetch() {
        System.out.println("\n\n======= JOIN FETCH로 상품과 연관 엔티티 함께 조회 시작 (1개의 쿼리 예상) =======");
        List<Product> productsWithDetails = em.createQuery(
                        "SELECT DISTINCT p FROM Product p " +
                                "JOIN FETCH p.seller " +
                                "JOIN FETCH p.category " +
                                "JOIN FETCH p.region", Product.class)
                .getResultList();
        System.out.println("======= JOIN FETCH 조회 완료: " + productsWithDetails.size() + "개 상품 =======\n");

        assertThat(productsWithDetails).hasSize(5);

        // 이미지 조회를 위한 단일 쿼리 사용
        System.out.println("\n======= 이미지 한번에 조회 시작 (1개의 쿼리 예상) =======");
        List<Long> productIds = productsWithDetails.stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        List<Image> allImages = em.createQuery(
                        "SELECT i FROM Image i WHERE i.product.id IN :productIds ORDER BY i.product.id, i.order", Image.class)
                .setParameter("productIds", productIds)
                .getResultList();
        System.out.println("======= 이미지 조회 완료: " + allImages.size() + "개 이미지 =======\n");

        assertThat(allImages).hasSize(25); // 5개 상품 * 5개 이미지

        // 결과 표시
        Map<Long, List<Image>> imagesByProductId = allImages.stream()
                .collect(Collectors.groupingBy(image -> image.getProduct().getId()));

        System.out.println("\n======= 그룹화된 이미지 정보 =======");
        for (Product p : productsWithDetails) {
            List<Image> images = imagesByProductId.get(p.getId());
            System.out.println(p.getTitle() + "의 이미지 " + images.size() + "개:");
            images.forEach(image -> System.out.println("  - " + image.getImageUrl()));
            assertThat(images).hasSize(5);
        }
        System.out.println("\n======= JOIN FETCH 테스트 완료 (총 쿼리 2개 실행됨) =======");
    }

    @Test
    @DisplayName("N+1 문제 해결 테스트: 최적화된 리포지토리 메서드 사용")
    void testSolveNPlus1WithOptimizedRepositoryMethod() {
        // given: 기존 setUp에서 준비됨
        em.flush();
        em.clear();

        // when: 최적화된 메서드 사용
        System.out.println("\n\n======= 최적화된 방법으로 상품 조회 시작 (1개의 쿼리 예상) =======");
        List<Product> productsWithDetails = productRepository.findAllWithDetails();
        System.out.println("======= 최적화된 상품 조회 완료: " + productsWithDetails.size() + "개 상품 =======\n");

        assertThat(productsWithDetails).hasSize(5);

        // ID 목록 추출
        List<Long> productIds = productsWithDetails.stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        // 한 번의 쿼리로 모든 이미지 조회
        System.out.println("\n======= 최적화된 방법으로 이미지 조회 시작 (1개의 쿼리 예상) =======");
        List<Image> allImages = imageRepository.findAllByProductIds(productIds);
        System.out.println("======= 최적화된 이미지 조회 완료: " + allImages.size() + "개 이미지 =======\n");

        // 검증
        assertThat(allImages).hasSize(25); // 5개 상품 * 5개 이미지

        // 각 상품별로 이미지 그룹화 (실제 서비스에서 사용할 수 있는 패턴)
        Map<Long, List<Image>> imagesByProductId = allImages.stream()
                .collect(Collectors.groupingBy(image -> image.getProduct().getId()));

        System.out.println("\n======= 그룹화된 이미지 정보 =======");
        for (Product p : productsWithDetails) {
            List<Image> images = imagesByProductId.get(p.getId());
            System.out.println(p.getTitle() + "의 이미지 " + images.size() + "개:");
            images.forEach(image -> System.out.println("  - " + image.getImageUrl()));
            assertThat(images).hasSize(5);
        }
        System.out.println("\n======= 최적화 메서드 테스트 완료 (총 쿼리 2개 실행됨) =======");
    }
}