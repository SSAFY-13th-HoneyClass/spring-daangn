package com.ssafy.daangn.service;

import com.ssafy.daangn.domain.*;
import com.ssafy.daangn.dto.SaleDetailRequestDto;
import com.ssafy.daangn.dto.SaleDetailResponseDto;
import com.ssafy.daangn.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleImageRepository saleImageRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SaleStatusRepository saleStatusRepository;
    private final ChatRoomService chatRoomService;
    private final FileService fileService;
    private final ChatRoomRepository chatRoomRepository;
    private final SaleLikeRepository saleLikeRepository;

    @Override
    public void save(SaleDetailRequestDto dto, MultipartFile thumbnail, List<MultipartFile> images) {
        // 1. 필수 엔티티들을 DB에서 조회
        User user = userRepository.findByNo(dto.getUserNo())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + dto.getUserNo()));

        // 2. Category와 SaleStatus는 기존에 존재하는지 확인 후 조회하거나 생성
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name(dto.getCategory())
                        .description(dto.getCategory()) // 기본값으로 name과 동일하게 설정
                        .build()));

        SaleStatus status = saleStatusRepository.findById(dto.getStatus())
                .orElseGet(() -> saleStatusRepository.save(SaleStatus.builder()
                        .name(dto.getStatus())
                        .description(dto.getStatus()) // 기본값으로 name과 동일하게 설정
                        .build()));

        // 3. 파일 업로드 처리
        String thumbnailUrl =null;
        if (thumbnail != null && !thumbnail.isEmpty()) {
            thumbnailUrl = fileService.upload(thumbnail);
        }

        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    imageUrls.add(fileService.upload(image));
                }
            }
        }

        // 4. Sale 엔티티 생성 및 저장
        Sale sale = Sale.builder()
                .user(user)
                .category(category)
                .status(status)
                .title(dto.getTitle())
                .content(dto.getContent())
                .price(dto.getPrice())
                .discount(dto.getDiscount())
                .isPriceSuggestible(dto.getIsPriceSuggestible())
                .thumbnail(thumbnailUrl)
                .viewCount(0) // 초기값 설정
                .likeCount(0) // 초기값 설정
                .chatCount(0) // 초기값 설정
                .address(dto.getAddress())
                .addressDetail(dto.getAddressDetail())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();

        Sale savedSale = saleRepository.save(sale);

        // 5. SaleImage 엔티티들 저장
        for (String imageUrl : imageUrls) {
            SaleImage saleImage = SaleImage.builder()
                    .sale(savedSale)
                    .imageUrl(imageUrl)
                    .build();
            saleImageRepository.save(saleImage);
        }
    }

    @Override
    public void update(SaleDetailRequestDto dto, MultipartFile thumbnail, List<MultipartFile> images) {
        // 1. 기존 Sale 조회
        Sale existingSale = saleRepository.findByNo(dto.getNo())
                .orElseThrow(() -> new EntityNotFoundException("판매글을 찾을 수 없습니다: " + dto.getNo()));

        // 2. Category와 SaleStatus 조회/생성
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name(dto.getCategory())
                        .description(dto.getCategory())
                        .build()));

        SaleStatus status = saleStatusRepository.findById(dto.getStatus())
                .orElseGet(() -> saleStatusRepository.save(SaleStatus.builder()
                        .name(dto.getStatus())
                        .description(dto.getStatus())
                        .build()));

        // 3. 기존 이미지들 삭제
        List<SaleImage> prevImages = saleImageRepository.findBySaleNo(dto.getNo());
        for (SaleImage image : prevImages) {
            fileService.delete(image.getImageUrl());
        }
        saleImageRepository.deleteBySaleNo(dto.getNo());

        // 4. 새 파일 업로드
        String thumbnailUrl = existingSale.getThumbnail(); // 기존 썸네일 유지
        if (thumbnail != null && !thumbnail.isEmpty()) {
            if (existingSale.getThumbnail() != null) {
                fileService.delete(existingSale.getThumbnail()); // 기존 썸네일 삭제
            }
            thumbnailUrl = fileService.upload(thumbnail);
        }

        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    imageUrls.add(fileService.upload(image));
                }
            }
        }

        // 5. Sale 엔티티 업데이트
        Sale updatedSale = existingSale.toBuilder()
                .category(category)
                .status(status)
                .title(dto.getTitle())
                .content(dto.getContent())
                .price(dto.getPrice())
                .discount(dto.getDiscount())
                .isPriceSuggestible(dto.getIsPriceSuggestible())
                .thumbnail(thumbnailUrl)
                .address(dto.getAddress())
                .addressDetail(dto.getAddressDetail())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();

        Sale savedSale = saleRepository.save(updatedSale);

        // 6. 새 이미지들 저장
        for (String imageUrl : imageUrls) {
            SaleImage saleImage = SaleImage.builder()
                    .sale(savedSale)
                    .imageUrl(imageUrl)
                    .build();
            saleImageRepository.save(saleImage);
        }
    }

    @Override
    public void delete(Long no) {
        Sale sale = saleRepository.findByNo(no)
                .orElseThrow(() -> new EntityNotFoundException("판매글을 찾을 수 없습니다: " + no));

        // 1. 연관된 채팅방들 삭제
        List<ChatRoom> chatRooms = chatRoomRepository.findBySaleNo(no);
        chatRoomRepository.deleteAll(chatRooms);

        // 2. 좋아요 삭제
        saleLikeRepository.deleteBySaleNo(no);

        // 3. 이미지 파일들 삭제
        List<SaleImage> saleImages = saleImageRepository.findBySaleNo(no);
        for (SaleImage image : saleImages) {
            fileService.delete(image.getImageUrl());
        }
        saleImageRepository.deleteBySaleNo(no);

        // 4. 썸네일 삭제
        if (sale.getThumbnail() != null) {
            fileService.delete(sale.getThumbnail());
        }

        // 5. Sale 삭제
        saleRepository.delete(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sale> findByUser_IdOrderByCreatedAtDesc(String userId) {
        return saleRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sale> findByTitleContainingOrContentContainingOrderByCreatedAtDesc(String titleKeyword, String contentKeyword) {
        return saleRepository.findByTitleContainingOrContentContainingOrderByCreatedAtDesc(titleKeyword, contentKeyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sale> findByLocationNear(double latitude, double longitude, double maxDistanceKm) {
        return saleRepository.findByLocationNear(latitude, longitude, maxDistanceKm);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleDetailResponseDto findByNo(long no) {
        Sale sale = saleRepository.findByNo(no);
        if (sale == null) {
            throw new EntityNotFoundException("판매글을 찾을 수 없습니다: " + no);
        }
        List<SaleImage> images = saleImageRepository.findBySaleNo(no);
        return new SaleDetailResponseDto(sale, images);
    }
}