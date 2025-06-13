package com.ssafy.springdaangn.exception;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(String message) {
        super(message);
    }

    public AddressNotFoundException(Long addressId) {
        super("주소를 찾을 수 없습니다. ID: " + addressId);
    }
}