package io.github.caiohbs.authentication.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ForgotPasswordDTO(
        @NotNull
        @NotEmpty
        String username
) {
}
