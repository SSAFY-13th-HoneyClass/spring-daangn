// src/main/java/com/ssafy/spring_boot/manner/service/MannerRatingService.java
package com.ssafy.spring_boot.manner.service;

import com.ssafy.spring_boot.manner.dto.MannerDetailDTO;
import com.ssafy.spring_boot.manner.dto.MannerRatingDTO;

import java.util.List;

public interface MannerRatingService {
    /**
     * 특정 사용자에 대한 매너 평가 등록
     * @param ratedUserId 평가받는 사용자 ID
     * @param raterUserId 평가하는 사용자 ID
     * @param detailId 매너 평가 항목 ID
     * @return 등록된 매너 평가 정보
     */
    MannerRatingDTO rateUserManner(Long ratedUserId, Long raterUserId, Long detailId);

    /**
     * 특정 사용자에 대한 매너 평가 목록 조회
     * @param userId 평가받은 사용자 ID
     * @return 매너 평가 목록
     */
    List<MannerRatingDTO> getUserMannerRatings(Long userId);

    /**
     * 매너 평가 항목 목록 조회
     * @return 매너 평가 항목 목록
     */
    List<MannerDetailDTO> getMannerDetailList();
}