package com.ssafy.springdaangn.service;

import com.ssafy.springdaangn.domain.Address;
import com.ssafy.springdaangn.domain.User;
import com.ssafy.springdaangn.exception.AddressNotFoundException;
import com.ssafy.springdaangn.exception.UserNotFoundException;
import com.ssafy.springdaangn.repository.AddressRepository;
import com.ssafy.springdaangn.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public Address addAddress(Long userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        address.setUser(user);
        return addressRepository.save(address);
    }

    public List<Address> getAddressesByUser(Long userId) {
        // 사용자 존재 여부 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Address address = addressRepository.findByUser_UserId(userId);
        if (address == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(address);
    }

    public void removeAddress(Long addressId) {
        Address addr = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));
        addressRepository.delete(addr);
    }
}