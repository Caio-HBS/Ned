package io.github.caiohbs.authentication.model;

public record RequestContext(
        String userAgent,
        String deviceType,
        String operatingSystem
) {
}
