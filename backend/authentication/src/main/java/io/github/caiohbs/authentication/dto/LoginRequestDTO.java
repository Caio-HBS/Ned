package io.github.caiohbs.authentication.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO(
        @NotNull
        @NotNull
        String username,
        @NotNull
        @NotEmpty
        String password
) {
}
