package io.github.caiohbs.authentication.model;

import io.github.caiohbs.authentication.model.enums.EmailActionType;

public record GenericEmail(
        Long userId,
        String email,
        String name,
        String token,
        RequestContext requestContext,
        EmailActionType actionType
) {
}
