package io.github.caiohbs.authentication.controller;

import io.github.caiohbs.authentication.dto.ReadUserDTO;
import io.github.caiohbs.authentication.dto.UpdateUserDTO;
import io.github.caiohbs.authentication.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // TODO: Group managers can access and will only see USERs in the group. ADMIN will see everyone.
    @GetMapping("/users")
    public ResponseEntity<List<ReadUserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    // TODO: Group managers can access themselves and USERs in the group. ADMIN always has access.
    @GetMapping("/users/{id}")
    public ResponseEntity<ReadUserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // TODO: USERs can only update their own profile. ADMIN always has access.
    //TODO: Add updateUser()
    @PutMapping("/users/{id}")
    public ResponseEntity<ReadUserDTO> updateUser(
            @Valid @PathVariable Long id, @RequestBody UpdateUserDTO updateUserDTO
    ) {
        return ResponseEntity.ok(userService.updateUser(updateUserDTO, id));
    }

    // TODO: Owners can always access their own profile, ADMIN always has access
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    // TODO: Group managers can access and will only see USERs in the group. ADMIN will see everyone.
    @GetMapping("/users/group/{group}")
    public ResponseEntity<List<ReadUserDTO>> getAllUsersByGroup(@PathVariable String group) {
        return ResponseEntity.ok(userService.getAllInGroup(group));
    }

}
