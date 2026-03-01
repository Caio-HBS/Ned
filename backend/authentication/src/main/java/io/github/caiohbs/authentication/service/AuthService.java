package io.github.caiohbs.authentication.service;

import io.github.caiohbs.authentication.dto.TokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public TokenResponseDTO login(String username, String password) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        return new TokenResponseDTO(tokenService.generateToken(authentication.getName()));
    }

    public TokenResponseDTO refresh(String authorizationHeader) {
        String newToken = tokenService.refreshFromAuthorizationHeader(authorizationHeader);
        return new TokenResponseDTO(newToken);
    }

}
