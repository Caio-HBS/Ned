package io.github.caiohbs.authentication.annotation;

import io.github.caiohbs.authentication.validators.PasswordBlacklistValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordBlacklistValidator.class)
public @interface ValidPasswordBlacklist {
    String message() default "This password is too common. Try another one.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
