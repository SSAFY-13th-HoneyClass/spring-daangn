package com.ssafy.springdaangn.dto;

import com.ssafy.springdaangn.domain.Address;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressDto {
    private Long addressId;
    private String neighborhood;
    private UserDto user;

    // 정적 팩토리 메서드 - Entity -> DTO
    public static AddressDto from(Address address) {
        return AddressDto.builder()
                .addressId(address.getAddressId())
                .neighborhood(address.getNeighborhood())
                .user(address.getUser() != null ? UserDto.from(address.getUser()) : null)
                .build();
    }

    // 정적 팩토리 메서드 - 간단한 정보만
    public static AddressDto fromSimple(Address address) {
        return AddressDto.builder()
                .addressId(address.getAddressId())
                .neighborhood(address.getNeighborhood())
                .build();
    }

    // 생성용 요청 DTO
    @Getter
    @Builder
    public static class CreateRequest {
        private String neighborhood;

        public static CreateRequest of(String neighborhood) {
            return CreateRequest.builder()
                    .neighborhood(neighborhood)
                    .build();
        }

        public Address toEntity() {
            return Address.builder()
                    .neighborhood(this.neighborhood)
                    .build();
        }
    }
}