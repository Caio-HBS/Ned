package io.github.caiohbs.authentication.dto;

public record CreateOrReadAddressDTO(
        String street,
        String number,
        String zipCode,
        String city,
        String state,
        boolean mainAddress
) {
}
