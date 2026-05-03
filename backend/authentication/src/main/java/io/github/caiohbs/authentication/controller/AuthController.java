package io.github.caiohbs.authentication.controller;

import io.github.caiohbs.authentication.dto.*;
import io.github.caiohbs.authentication.service.AuthService;
import io.github.caiohbs.authentication.service.UserService;
import io.github.caiohbs.authentication.service.UserTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static io.github.caiohbs.authentication.model.enums.UserTokenType.EMAIL_VERIFICATION;
import static io.github.caiohbs.authentication.model.enums.UserTokenType.RESET_PASSWORD;

@Tag(name="Authentication", description="Endpoints for authentication")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final UserTokenService userTokenService;

    @Operation(summary="Register a new user")
    @PostMapping("/register")
    public ResponseEntity<ReadUserDTO> register(@Valid @RequestBody CreateUserDTO userDTO) {
        ReadUserDTO createdUser = userService.create(userDTO);
        URI location = URI.create("/users/users/" + createdUser.userId());
        return ResponseEntity.created(location).body(createdUser);
    }

    @Operation(summary="Authenticate a user")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> authenticate(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO.username(), loginRequestDTO.password()));
    }

    @Operation(summary="Refresh a user's token (has to be logged in)")
    @GetMapping("/refresh")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TokenResponseDTO> refreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        return ResponseEntity.ok(authService.refresh(authorization));
    }

    @Operation(summary="Activate a user's account")
    @GetMapping("/activate-user")
    public ResponseEntity<ReadUserDTO> activateUser(@RequestParam("token") String token) {
        return ResponseEntity.ok(userTokenService.consumeToken(EMAIL_VERIFICATION, token, null));
    }

    @Operation(summary="Request a password reset")
    @PostMapping("/forgot-password")
    public ResponseEntity<GenericResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        String returnForgotPasswordMessage = userTokenService.createResetPasswordToken(forgotPasswordDTO.username());
        return ResponseEntity.ok(new GenericResponseDTO(returnForgotPasswordMessage));
    }

    @Operation(summary="Reset a user's password")
    @PostMapping("/password-reset")
    public ResponseEntity<ReadUserDTO> resetPassword(
            @RequestParam("token") String token,
            @Valid @RequestBody ResetPasswordDTO resetPasswordDTO
    ) {
        return ResponseEntity.ok(userTokenService.consumeToken(RESET_PASSWORD, token, resetPasswordDTO));
    }

}
