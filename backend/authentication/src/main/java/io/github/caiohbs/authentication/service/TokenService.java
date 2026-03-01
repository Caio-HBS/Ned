package io.github.caiohbs.authentication.service;

import io.github.caiohbs.authentication.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${NED.security.jwt.config.issuer}")
    private String ISSUER;
    @Value("${NED.security.jwt.config.expires_in_seconds}")
    private Long EXPIRES_IN_SECONDS;

    private final JwtEncoder encoder;
    private final UserService userService;
    private final Clock clock;

    public String generateToken(String email) {
        User user = userService.getUserByEmailConvertingOptional(email);

        Instant now = Instant.now(clock);

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        System.out.println("ia" + now);
        System.out.println("ea" + now.plusSeconds(EXPIRES_IN_SECONDS));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(EXPIRES_IN_SECONDS))
                .subject(email)
                .claim("role", user.getRole().name())
                //.claim("scope", user.getAuthorities().stream().toList()) TODO
                .build();

        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

}
