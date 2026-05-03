package io.github.caiohbs.authentication.controller;

import io.github.caiohbs.authentication.dto.CreateAddressDTO;
import io.github.caiohbs.authentication.dto.ReadAddressDTO;
import io.github.caiohbs.authentication.dto.UpdateAddressDTO;
import io.github.caiohbs.authentication.service.AddressService;
import io.github.caiohbs.authentication.util.SortParamParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name="Address", description="Endpoints for managing addresses")
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final SortParamParser sortParamParser;

    @Operation(summary="Create a new address")
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

    @Operation(
            summary="List all addresses",
            description="You have to be an admin to access this endpoint."
    )
    @GetMapping("/address")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReadAddressDTO>> getAllAddresses(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size,
            @RequestParam(defaultValue="addressId,desc") String sort
    ) {
        return ResponseEntity.ok(
                addressService.getAllAddresses(PageRequest.of(page, size, sortParamParser.parseSortParamForAddress(sort)))
        );
    }

    @Operation(
            summary="Get a single address by ID",
            description="You have to be the owner of the resource in order to access this endpoint."
    )
    @GetMapping("/address/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isAddressOwner(#addressId, authentication)")
    public ResponseEntity<ReadAddressDTO> getSingleAddressById(@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.getSingleAddress(addressId));
    }

    @Operation(
            summary="List all addresses of a user",
            description="You have to be the owner of the resource in order to access this endpoint."
    )
    @GetMapping("/address/user-address/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isOwner(#userId, authentication)")
    public ResponseEntity<Page<ReadAddressDTO>> getAddressesByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size,
            @RequestParam(defaultValue="addressId,desc") String sort
    ) {
        return ResponseEntity.ok(
                addressService
                        .getAllAddressesByUser(userId, PageRequest.of(page, size, sortParamParser.parseSortParamForAddress(sort)))
        );
    }

    @Operation(
            summary="Update an address",
            description="You have to be the owner of the resource in order to access this endpoint."
    )
    @PutMapping("/address/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isAddressOwner(#addressId, authentication)")
    public ResponseEntity<ReadAddressDTO> updateAddress(
            @RequestBody UpdateAddressDTO updateAddressDTO, @PathVariable Long addressId
    ) {
        return ResponseEntity.ok(addressService.updateAddress(updateAddressDTO, addressId));
    }

    @Operation(
            summary="Delete an address",
            description="You have to be the owner of the resource in order to access this endpoint."
    )
    @DeleteMapping("/address/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isAddressOwner(#addressId, authentication)")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

}
