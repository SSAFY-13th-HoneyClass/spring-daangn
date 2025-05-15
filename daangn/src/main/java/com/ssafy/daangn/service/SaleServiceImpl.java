package com.ssafy.daangn.service;

import com.ssafy.daangn.domain.*;
import com.ssafy.daangn.dto.SaleDetailRequestDto;
import com.ssafy.daangn.dto.SaleDetailResponseDto;
import com.ssafy.daangn.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
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


    public void save(SaleDetailRequestDto dto, MultipartFile thumbnail, List<MultipartFile> images) {
        User user = userRepository.findByNo(dto.getUserNo()).orElseThrow();
        Category category = Category.builder().name(dto.getCategory()).build();
        SaleStatus status = SaleStatus.builder().name(dto.getStatus()).build();
        String thumbnailUrl = fileService.upload(thumbnail);
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            imageUrls.add(fileService.upload(image));
        }

        Sale sale = Sale.builder()
                .user(user)
                .category(category)
                .status(status)
                .title(dto.getTitle())
                .content(dto.getContent())
                .price(dto.getPrice())
                .discount(dto.getDiscount())
                .isPriceSuggestible(dto.getIsPriceSuggestible())
                .thumbnail(thumbnailUrl) // URL로 저장
                .viewCount(dto.getViewCount())
                .likeCount(dto.getLikeCount())
                .chatCount(dto.getChatCount())
                .address(dto.getAddress())
                .addressDetail(dto.getAddressDetail())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();

        Sale saved = saleRepository.save(sale);

        for (String imageUrl : imageUrls) {
            SaleImage image = SaleImage.builder()
                    .sale(saved)
                    .imageUrl(imageUrl)
                    .build();
            saleImageRepository.save(image);
        }
    }

    @Override
    public void update(SaleDetailRequestDto dto, MultipartFile thumbnail, List<MultipartFile> images) {
//            User user = userRepository.findByNo(dto.getUserNo()).orElseThrow();
        Category category = Category.builder().name(dto.getCategory()).build();
        SaleStatus status = SaleStatus.builder().name(dto.getStatus()).build();

        String thumbnailUrl = fileService.upload(thumbnail);

        List<SaleImage> prevImages = saleImageRepository.findBySaleNo(dto.getNo());
        for (SaleImage image : prevImages) {
            fileService.delete(image.getImageUrl());
        }
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            imageUrls.add(fileService.upload(image));
        }

        Sale sale = saleRepository.findByNo(dto.getUserNo()).orElseThrow();
        Sale newSale = sale.toBuilder()
//                    .user(user)
                .category(category)
                .status(status)
                .title(dto.getTitle())
                .content(dto.getContent())
                .price(dto.getPrice())
                .discount(dto.getDiscount())
                .isPriceSuggestible(dto.getIsPriceSuggestible())
                .thumbnail(thumbnailUrl) // URL로 저장
                .viewCount(dto.getViewCount())
                .likeCount(dto.getLikeCount())
                .chatCount(dto.getChatCount())
                .address(dto.getAddress())
                .addressDetail(dto.getAddressDetail())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();

        Sale saved = saleRepository.save(sale);

        for (String imageUrl : imageUrls) {
            SaleImage image = SaleImage.builder()
                    .sale(saved)
                    .imageUrl(imageUrl)
                    .build();
            saleImageRepository.save(image);
        }
    }

    @Override
    public void delete(Long no) {
        Sale sale = saleRepository.findByNo(no).orElseThrow();
        List<ChatRoom> chatRooms = chatRoomRepository.findBySaleNo(no);
        chatRoomRepository.deleteAll(chatRooms);
        saleLikeRepository.deleteBySaleNo(no);

        List<SaleImage> saleImages = saleImageRepository.findBySaleNo(no);
        for (SaleImage image : saleImages) {
            fileService.delete(image.getImageUrl());
        }
        saleImageRepository.deleteBySaleNo(no);
    }

    @Override
    public List<Sale> findByUser_IdOrderByCreatedAtDesc(String userId) {
        return saleRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Sale> findByTitleContainingOrContentContainingOrderByCreatedAtDesc(String titleKeyword, String contentKeyword) {
        return saleRepository.findByTitleContainingOrContentContainingOrderByCreatedAtDesc(titleKeyword, contentKeyword);
    }

    @Override
    public List<Sale> findByLocationNear(double latitude, double longitude, double maxDistanceKm) {
        return saleRepository.findByLocationNear(latitude, longitude, maxDistanceKm);
    }

    @Override
    public SaleDetailResponseDto findByNo(long no) {
        Sale sale = saleRepository.findByNo(no);
        List<SaleImage> images = saleImageRepository.findBySaleNo(no);
        return new SaleDetailResponseDto(sale, images);
    }
}
