package io.github.caiohbs.authentication.model;

import io.github.caiohbs.authentication.model.enums.EmailActionType;

import java.time.LocalDateTime;

public record GenericEmail(
        Long userId,
        String email,
        String name,
        String token,
        LocalDateTime sentAt,
        EmailActionType actionType,
        RequestContext requestContext
) {
}
