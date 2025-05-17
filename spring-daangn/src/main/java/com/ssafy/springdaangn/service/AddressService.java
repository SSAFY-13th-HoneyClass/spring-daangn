package com.ssafy.springdaangn.service;

import com.ssafy.springdaangn.Domain.Address;
import com.ssafy.springdaangn.Domain.User;
import com.ssafy.springdaangn.Repository.AddressRepository;
import com.ssafy.springdaangn.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        address.setUser(user);
        return addressRepository.save(address);
    }

    public List<Address> getAddressesByUser(Long userId) {
        return Collections.singletonList(addressRepository.findByUser_UserId(userId));
    }

    public void removeAddress(Long addressId) {
        Address addr = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found: " + addressId));
        addressRepository.delete(addr);
    }
}
