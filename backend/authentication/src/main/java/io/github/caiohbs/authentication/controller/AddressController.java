package io.github.caiohbs.authentication.controller;

import io.github.caiohbs.authentication.dto.CreateAddressDTO;
import io.github.caiohbs.authentication.dto.ReadAddressDTO;
import io.github.caiohbs.authentication.dto.UpdateAddressDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class AddressController {

    @PostMapping("/address")
    // TODO: Everyone can create an address.
    public ResponseEntity<ReadAddressDTO> createAddress(
            @Valid @RequestBody CreateAddressDTO addressDTO
    ) {
        return null;
    }

    @GetMapping("/address")
    // TODO: Only ADMIN can access all addresses.
    public ResponseEntity<List<ReadAddressDTO>> getAllAddresses(Long addressId) {
        return null;
    }

    @GetMapping("/address/{addressId}")
    // TODO: If USER is owner: access granted. If ADMIN: access granted. Else: access denied.
    public ResponseEntity<ReadAddressDTO> getSingleAddressById(@PathVariable Long addressId) {
        return null;
    }

    @GetMapping("/address/user-address/{userId}")
    // TODO: Only USER can access their own addresses. ADMIN always has access.
    public ResponseEntity<List<ReadAddressDTO>> getAddressesByUser(@PathVariable Long userId) {
        return null;
    }

    @PutMapping("/address/{addressId}")
    // TODO: Only USER can update their own address. ADMIN always has access.
    public ResponseEntity<ReadAddressDTO> updateAddress(
            @RequestBody UpdateAddressDTO updateAddressDTO, @PathVariable Long addressId
    ) {
        return null;
    }

    @DeleteMapping("/address/{addressId}")
    // TODO: Only USER can delete their own address. ADMIN always has access.
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        return null;
    }

}
