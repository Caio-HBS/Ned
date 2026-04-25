package io.github.caiohbs.authentication.dto;

import io.github.caiohbs.authentication.annotation.ValidPasswordBlacklist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResetPasswordDTO(
        @NotNull
        @NotBlank
        @Size(min=8, max=255)
        @ValidPasswordBlacklist
        String password
) {
}
