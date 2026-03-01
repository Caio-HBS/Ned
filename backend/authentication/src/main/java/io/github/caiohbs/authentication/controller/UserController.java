package io.github.caiohbs.authentication.controller;

import io.github.caiohbs.authentication.dto.ReadUserDTO;
import io.github.caiohbs.authentication.dto.UpdateUserDTO;
import io.github.caiohbs.authentication.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReadUserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isOwner(#id, authentication)")
    @GetMapping("/users/{id}")
    public ResponseEntity<ReadUserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isOwner(#id, authentication)")
    public ResponseEntity<ReadUserDTO> updateUser(
            @Valid @PathVariable Long id, @RequestBody UpdateUserDTO updateUserDTO
    ) {
        return ResponseEntity.ok(userService.updateUser(updateUserDTO, id));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isOwner(#id, authentication)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

}
