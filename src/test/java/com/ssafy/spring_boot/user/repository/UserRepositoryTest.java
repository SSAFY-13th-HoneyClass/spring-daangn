package com.ssafy.spring_boot.user.repository;

import com.ssafy.spring_boot.region.domain.Region;
import com.ssafy.spring_boot.region.repository.RegionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // ✅ 이 상태면 자동으로 H2 사용됨
class UserRepositoryTest {

    @Autowired
    private RegionRepository regionRepository;

    @Test
    @DisplayName("Region 저장 테스트")
    void testSaveRegion() {
        Region region = regionRepository.save(Region.builder().name("서울").build());

        List<Region> all = regionRepository.findAll();
        assertThat(all).isNotEmpty();
        assertThat(all.get(0).getName()).isEqualTo("서울");

        System.out.println("✅ 저장된 region: " + all.get(0).getName());
    }
}
