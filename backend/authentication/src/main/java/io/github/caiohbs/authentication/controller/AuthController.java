package io.github.caiohbs.authentication.controller;

import io.github.caiohbs.authentication.dto.CreateUserDTO;
import io.github.caiohbs.authentication.dto.ReadUserDTO;
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

    @PostMapping("/register")
    public ResponseEntity<ReadUserDTO> register(@Valid @RequestBody CreateUserDTO userDTO) {
        return ResponseEntity.ok(userService.create(userDTO));
    }

    //TODO: Add authenticate()
    @PostMapping("/login")
    public void authenticate() {
        return;
    }

    //TODO: Add refreshToken()
    @PostMapping("/refresh")
    public void refreshToken() {
        return;
    }

}
