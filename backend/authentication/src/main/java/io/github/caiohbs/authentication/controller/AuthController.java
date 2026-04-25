package io.github.caiohbs.authentication.controller;

import io.github.caiohbs.authentication.dto.*;
import io.github.caiohbs.authentication.service.AuthService;
import io.github.caiohbs.authentication.service.UserService;
import io.github.caiohbs.authentication.service.UserTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static io.github.caiohbs.authentication.model.enums.UserTokenType.EMAIL_VERIFICATION;
import static io.github.caiohbs.authentication.model.enums.UserTokenType.RESET_PASSWORD;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final UserTokenService userTokenService;

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

    @GetMapping("/activate-user")
    public ResponseEntity<ReadUserDTO> activateUser(@RequestParam("token") String token) {
        return ResponseEntity.ok(userTokenService.consumeToken(EMAIL_VERIFICATION, token, null));
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<GenericResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        String returnForgotPasswordMessage = userTokenService.createResetPasswordToken(forgotPasswordDTO.username());
        return ResponseEntity.ok(new GenericResponseDTO(returnForgotPasswordMessage));
    }

    @PostMapping("/password-reset")
    public ResponseEntity<ReadUserDTO> resetPassword(
            @RequestParam("token") String token,
            @Valid @RequestBody ResetPasswordDTO resetPasswordDTO
    ) {
        return ResponseEntity.ok(userTokenService.consumeToken(RESET_PASSWORD, token, resetPasswordDTO));
    }

}
