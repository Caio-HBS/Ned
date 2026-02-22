package io.github.caiohbs.authentication.validators;

import io.github.caiohbs.authentication.annotation.ValidPasswordBlacklist;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PasswordBlacklistValidator implements ConstraintValidator<ValidPasswordBlacklist, String> {

    private final PasswordBlacklistChecker blacklistChecker;

    // O Spring injeta o componente que criamos anteriormente
    public PasswordBlacklistValidator(PasswordBlacklistChecker blacklistChecker) {
        this.blacklistChecker = blacklistChecker;
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            return true; // Deixe o @NotBlank cuidar disso
        }

        // Se a senha estiver na lista, a validação FALHA (retorna false)
        return !blacklistChecker.isBlacklisted(password);
    }
}
