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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ReadUserDTO> register(@Valid @RequestBody CreateUserDTO userDTO) {
        return ResponseEntity.ok(userService.create(userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> authenticate(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO.username(), loginRequestDTO.password()));
    }

    //TODO: Add refreshToken()
    @PostMapping("/refresh")
    public void refreshToken() {
        return;
    }

}
