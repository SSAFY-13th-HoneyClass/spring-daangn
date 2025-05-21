package com.ssafy.Daangn.Service;

import com.ssafy.Daangn.Domain.Location;
import com.ssafy.Daangn.Repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    // 전체 지역 조회
    public List<Location> findAllLocations() {
        return locationRepository.findAll();
    }

    // 지역명으로 검색
    public List<Location> findLocationsByName(String locationName) {
        return locationRepository.findByLocationName(locationName);
    }

}