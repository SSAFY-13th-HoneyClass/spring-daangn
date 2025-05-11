package com.ssafy.spring_boot.repositoryTest;

import com.ssafy.spring_boot.category.domain.Category;
import com.ssafy.spring_boot.category.repository.CategoryRepository;
import com.ssafy.spring_boot.product.domain.Product;
import com.ssafy.spring_boot.product.repository.ProductRepository;
import com.ssafy.spring_boot.region.domain.Region;
import com.ssafy.spring_boot.region.repository.RegionRepository;
import com.ssafy.spring_boot.user.domain.User;
import com.ssafy.spring_boot.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// 기본 내장 DB(H2)를 사용하지 않고, 실제 설정된 DB를 사용하도록 지정
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager em;
    // EntityManaber는 flush/clear 같은 작업으로 직접 영속성 컨텍스트 재이용

    @Test
    void product_save_and_select_test() {
        // given
        Region region = regionRepository.save(Region.builder().name("서울").build());
        Category category = categoryRepository.save(Category.builder().type("전자기기").build());
        User seller = userRepository.save(
                User.builder()
                        .region(region)
                        .nickname("판매자")
                        .temperature(36.5)
                        .build()
        );

        // when
        Product p1 = productRepository.save(Product.builder()
                .title("아이폰")
                .thumbnail("url1")
                .category(category)
                .region(region)
                .seller(seller)
                .build());
        Product p2 = productRepository.save(Product.builder()
                .title("갤럭시")
                .thumbnail("url2")
                .category(category)
                .region(region)
                .seller(seller)
                .build());
        Product p3 = productRepository.save(Product.builder()
                .title("노트북")
                .thumbnail("url3")
                .category(category)
                .region(region)
                .seller(seller)
                .build());

        em.flush();     // 쿼리 즉시 실행
        // 추가 설명 : 영속성 컨텍스트에 쌓여있는 변경사항을 DB에 강제로 반영
        // 테스트 중간에 진짜 DB에 저장되었는지 확인하고 싶을때 사용

        em.clear();     // 영속성 컨텍스트 초기화 (진짜 DB에서 가져오는지 확인용)
        // 현재 EntityManager가 관리하고 있는 1차 캐시를 초기화
        // 초기화하면 productRepository.findAll()을 실행할 때, JPA는 DB에 다시 조회 쿼리 날림
        // 목적 : findALl()에서 Db에서 진짜 가져오는지 확인하기 위해 캐시 비우기
        // then
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(3);
        products.forEach(System.out::println);
    }
}

