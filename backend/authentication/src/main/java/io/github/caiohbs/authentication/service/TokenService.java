package io.github.caiohbs.authentication.service;

import io.github.caiohbs.authentication.exception.CustomJwtException;
import io.github.caiohbs.authentication.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${NED.security.jwt.config.issuer}")
    private String ISSUER;
    @Value("${NED.security.jwt.config.expires_in_seconds}")
    private Long EXPIRES_IN_SECONDS;

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final UserService userService;
    private final Clock clock;

    public String generateToken(String email) {
        User user = userService.getUserByEmailConvertingOptional(email);

        Instant now = Instant.now(clock);

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        List<String> scope = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(EXPIRES_IN_SECONDS))
                .subject(email)
                .claim("role", user.getRole().name())
                .claim("scope", scope)
                .build();

        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String refreshFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new CustomJwtException("Missing Authorization header.");
        }

        String prefix = "Bearer ";
        if (!authorizationHeader.startsWith(prefix)) {
            throw new CustomJwtException("Invalid Authorization header. Expected: Bearer <token>.");
        }

        String token = authorizationHeader.substring(prefix.length()).trim();
        if (token.isEmpty()) {
            throw new CustomJwtException("Bearer token is empty.");
        }

        Jwt jwt = decoder.decode(token);

        String email = jwt.getSubject();
        if (email == null || email.isBlank()) {
            throw new CustomJwtException("Token subject is missing.");
        }

        return generateToken(email);
    }

}
