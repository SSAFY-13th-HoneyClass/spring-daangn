package com.ssafy.spring_boot.image.repository;

import com.ssafy.spring_boot.image.domain.Image;
import com.ssafy.spring_boot.manner.domain.MannerDetail;
import com.ssafy.spring_boot.manner.domain.MannerRating;
import com.ssafy.spring_boot.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
    List<Image> findAllByProduct_Id(Long productId);
    List<Image> findByProductIdOrderByOrderAsc(Long productId);
    // src/main/java/com/ssafy/spring_boot/image/repository/ImageRepository.java에 추가
    @Query("SELECT i FROM Image i WHERE i.product.id IN :productIds ORDER BY i.product.id, i.order")
    List<Image> findAllByProductIds(@Param("productIds") List<Long> productIds);
}
