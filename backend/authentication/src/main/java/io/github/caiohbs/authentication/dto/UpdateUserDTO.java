package io.github.caiohbs.authentication.dto;

import io.github.caiohbs.authentication.annotation.ValidPasswordBlacklist;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.*;

public record UpdateUserDTO(
        @NotNull
        @NotBlank
        Long userId,
        @NotNull
        @NotBlank
        @Email
        String newEmail,
        @NotNull
        @NotBlank
        @Size(min=8, max=255)
        @ValidPasswordBlacklist
        String newPassword,
        @NotNull
        @NotBlank
        String phoneNumber
) {
}
