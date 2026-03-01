package io.github.caiohbs.authentication.controller;

import io.github.caiohbs.authentication.dto.CreateUserDTO;
import io.github.caiohbs.authentication.dto.LoginRequestDTO;
import io.github.caiohbs.authentication.dto.ReadUserDTO;
import io.github.caiohbs.authentication.dto.TokenResponseDTO;
import io.github.caiohbs.authentication.service.AuthService;
import io.github.caiohbs.authentication.service.TokenService;
import io.github.caiohbs.authentication.service.UserService;
import jakarta.validation.Valid;
import jdk.jfr.Registered;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ReadUserDTO> register(@Valid @RequestBody CreateUserDTO userDTO) {
        ReadUserDTO createdUser = userService.create(userDTO);
        URI location = URI.create("/users/users/" + createdUser.userId());
        return ResponseEntity.created(location).body(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> authenticate(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO.username(), loginRequestDTO.password()));
    }

    @PostMapping("/refresh")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TokenResponseDTO> refreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        return ResponseEntity.ok(authService.refresh(authorization));
    }

}
