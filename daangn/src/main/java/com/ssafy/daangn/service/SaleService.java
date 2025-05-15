package com.ssafy.daangn.service;

import com.ssafy.daangn.domain.Sale;
import com.ssafy.daangn.dto.SaleDetailRequestDto;
import com.ssafy.daangn.dto.SaleDetailResponseDto;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

public interface SaleService {
    void save(SaleDetailRequestDto dto, MultipartFile thumbnail, List<MultipartFile> images);
    // SaleDetailResponseDto save(SaleDetailResponseDto saleDetailDto);
    void update(SaleDetailRequestDto dto, MultipartFile thumbnail, List<MultipartFile> images);

//    SaleDetailResponseDto update(SaleDetailResponseDto saleDetailDto);
    void delete(Long no);

    List<Sale> findByUser_IdOrderByCreatedAtDesc(String userId);

    List<Sale> findByTitleContainingOrContentContainingOrderByCreatedAtDesc(String titleKeyword, String contentKeyword);

    List<Sale> findByLocationNear(double latitude, double longitude, double maxDistanceKm);

    SaleDetailResponseDto findByNo(long no);

}
