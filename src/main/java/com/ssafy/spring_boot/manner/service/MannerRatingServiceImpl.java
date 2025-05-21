package com.ssafy.spring_boot.manner.service;


import com.ssafy.spring_boot.manner.domain.MannerDetail;
import com.ssafy.spring_boot.manner.domain.MannerRating;
import com.ssafy.spring_boot.manner.dto.MannerDetailDTO;
import com.ssafy.spring_boot.manner.dto.MannerRatingDTO;
import com.ssafy.spring_boot.manner.repository.MannerDetailRepository;
import com.ssafy.spring_boot.manner.repository.MannerRatingRepository;
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
@Transactional
public class MannerRatingServiceImpl implements MannerRatingService {

    private final MannerRatingRepository mannerRatingRepository;
    private final MannerDetailRepository mannerDetailRepository;
    private final UserRepository userRepository;

    @Override
    public MannerRatingDTO rateUserManner(Long ratedUserId, Long raterUserId, Long detailId) {
        User ratedUser = userRepository.findById(ratedUserId)
                .orElseThrow(() -> new EntityNotFoundException("평가받을 사용자를 찾을 수 없습니다. ID: " + ratedUserId));

        User raterUser = userRepository.findById(raterUserId)
                .orElseThrow(() -> new EntityNotFoundException("평가하는 사용자를 찾을 수 없습니다. ID: " + raterUserId));

        MannerDetail mannerDetail = mannerDetailRepository.findById(detailId)
                .orElseThrow(() -> new EntityNotFoundException("매너 평가 항목을 찾을 수 없습니다. ID: " + detailId));

        // 매너 온도 업데이트 로직 (간단한 예시)
        ratedUser.setTemperature(ratedUser.getTemperature() + 0.1);
        userRepository.save(ratedUser);

        // 매너 평가 저장
        MannerRating mannerRating = mannerRatingRepository.save(MannerRating.builder()
                .ratedUser(ratedUser)
                .raterUser(raterUser)
                .mannerDetail(mannerDetail)
                .build());

        return convertToDTO(mannerRating);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MannerRatingDTO> getUserMannerRatings(Long userId) {
        List<MannerRating> ratings = mannerRatingRepository.findAllByRatedUserId(userId);
        return ratings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MannerDetailDTO> getMannerDetailList() {
        List<MannerDetail> details = mannerDetailRepository.findAll();
        return details.stream()
                .map(detail -> MannerDetailDTO.builder()
                        .id(detail.getId())
                        .content(detail.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    private MannerRatingDTO convertToDTO(MannerRating mannerRating) {
        return MannerRatingDTO.builder()
                .id(mannerRating.getId())
                .ratedUserId(mannerRating.getRatedUser().getId().longValue())
                .ratedUserNickname(mannerRating.getRatedUser().getNickname())
                .raterUserId(mannerRating.getRaterUser().getId().longValue())
                .raterUserNickname(mannerRating.getRaterUser().getNickname())
                .detailId(mannerRating.getMannerDetail().getId())
                .detailContent(mannerRating.getMannerDetail().getContent())
                .build();
    }
}