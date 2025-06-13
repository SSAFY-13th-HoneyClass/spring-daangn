package com.ssafy.springdaangn.controller;

import com.ssafy.springdaangn.domain.Address;
import com.ssafy.springdaangn.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AddressController {

    private final AddressService addressService;

    // 1. 새로운 주소 추가
    @PostMapping
    public ResponseEntity<Address> addAddress(
            @RequestParam Long userId,
            @RequestBody Address address) {
        Address createdAddress = addressService.addAddress(userId, address);
        return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
    }

    // 2. 특정 사용자의 주소 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Address>> getAddressesByUser(@PathVariable Long userId) {
        List<Address> addresses = addressService.getAddressesByUser(userId);
        return ResponseEntity.ok(addresses);
    }

    // 3. 주소 삭제
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> removeAddress(@PathVariable Long addressId) {
        addressService.removeAddress(addressId);
        return ResponseEntity.noContent().build();
    }
}