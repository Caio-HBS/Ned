package io.github.caiohbs.authentication.controller;

import io.github.caiohbs.authentication.dto.ReadUserDTO;
import io.github.caiohbs.authentication.dto.UpdateUserDTO;
import io.github.caiohbs.authentication.service.UserService;
import io.github.caiohbs.authentication.util.SortParamParser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SortParamParser sortParamParser;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReadUserDTO>> getAllUsers(
            @RequestParam(name="page", defaultValue="0") int page,
            @RequestParam(name="size", defaultValue="10") int size,
            @RequestParam(name="sort", defaultValue="userId,desc") String sort
    ) {
        return ResponseEntity.ok(userService.getAll(PageRequest.of(page, size, sortParamParser.parseSortParamForUser(sort))));
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
