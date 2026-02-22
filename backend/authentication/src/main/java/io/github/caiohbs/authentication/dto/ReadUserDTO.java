package io.github.caiohbs.authentication.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ReadUserDTO(
        Long userId,
        String email,
        String fullName,
        LocalDate birthday,
        String uniqueLocalIdentification,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CreateOrReadAddressDTO> addresses
) {
}
