package com.ssafy.spring_boot.image.repository;

import com.ssafy.spring_boot.image.domain.Image;
import com.ssafy.spring_boot.manner.domain.MannerDetail;
import com.ssafy.spring_boot.manner.domain.MannerRating;
import com.ssafy.spring_boot.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
    List<Image> findAllByProduct_Id(Long productId);
}
