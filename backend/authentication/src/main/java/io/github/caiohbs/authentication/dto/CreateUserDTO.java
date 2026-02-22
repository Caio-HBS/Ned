package io.github.caiohbs.authentication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.caiohbs.authentication.annotation.ValidPasswordBlacklist;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateUserDTO(
        @NotNull
        @NotBlank
        @Email
        String email,
        @NotNull
        @NotBlank
        @Size(min=8, max=255)
        @ValidPasswordBlacklist
        String password,
        @NotNull
        @NotBlank
        @Size(min=10, max=150)
        String fullName,
        @NotNull
        @NotBlank
        @Size(min=11, max=100)
        String uniqueLocalIdentification,
        @NotNull
        @NotBlank
        String phoneNumber,
        @NotNull
        @JsonFormat(pattern="yyyy-MM-dd")
        LocalDate birthday,
        CreateAddressDTO address
) {
}
