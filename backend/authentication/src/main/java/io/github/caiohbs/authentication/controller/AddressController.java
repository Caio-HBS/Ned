package io.github.caiohbs.authentication.controller;

import io.github.caiohbs.authentication.dto.CreateAddressDTO;
import io.github.caiohbs.authentication.dto.ReadAddressDTO;
import io.github.caiohbs.authentication.dto.UpdateAddressDTO;
import io.github.caiohbs.authentication.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/address")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReadAddressDTO> createAddress(
            @Valid @RequestBody CreateAddressDTO addressDTO,
            Authentication authentication
    ) {
        ReadAddressDTO created = addressService.createAddress(addressDTO, authentication.getName());
        URI location = URI.create("/api/v1/address/" + created.addressId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/address")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReadAddressDTO>> getAllAddresses(Long addressId) {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @GetMapping("/address/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isAddressOwner(#addressId, authentication)")
    public ResponseEntity<ReadAddressDTO> getSingleAddressById(@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.getSingleAddress(addressId));
    }

    @GetMapping("/address/user-address/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isOwner(#userId, authentication)")
    public ResponseEntity<List<ReadAddressDTO>> getAddressesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getAllAddressesByUser(userId));
    }

    @PutMapping("/address/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isAddressOwner(#addressId, authentication)")
    public ResponseEntity<ReadAddressDTO> updateAddress(
            @RequestBody UpdateAddressDTO updateAddressDTO, @PathVariable Long addressId
    ) {
        return ResponseEntity.ok(addressService.updateAddress(updateAddressDTO, addressId));
    }

    @DeleteMapping("/address/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isAddressOwner(#addressId, authentication)")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

}
