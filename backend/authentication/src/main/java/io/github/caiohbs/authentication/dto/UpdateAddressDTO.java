package io.github.caiohbs.authentication.dto;

public record UpdateAddressDTO(
        String street,
        String number,
        String zipCode,
        String city,
        String state,
        boolean mainAddress
) {
}