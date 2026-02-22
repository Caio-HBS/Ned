package io.github.caiohbs.authentication.dto;

public record ReadAddressDTO(
        Long addressId,
        String street,
        String number,
        String zipCode,
        String city,
        String state,
        boolean mainAddress
) {
}
