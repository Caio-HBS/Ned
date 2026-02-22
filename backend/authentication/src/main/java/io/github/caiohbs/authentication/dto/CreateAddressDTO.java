package io.github.caiohbs.authentication.dto;

public record CreateAddressDTO(
        String street,
        String number,
        String zipCode,
        String city,
        String state,
        boolean mainAddress
) {
}
