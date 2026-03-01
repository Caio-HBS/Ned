package io.github.caiohbs.authentication.config;

import io.github.caiohbs.authentication.model.Address;
import io.github.caiohbs.authentication.model.User;
import io.github.caiohbs.authentication.repository.AddressRepository;
import io.github.caiohbs.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("securityExpressions")
@RequiredArgsConstructor
public class SecurityExpressions {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public boolean isOwner(Long userId, Authentication authentication) {
        if (userId == null || authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName(); // JWT "name" = email
        if (email == null || email.isBlank()) {
            return false;
        }

        return userRepository.findById(userId)
                .map(User::getEmail)
                .map(ownerEmail -> ownerEmail.equalsIgnoreCase(email))
                .orElse(false);
    }

    public boolean isAddressOwner(Long addressId, Authentication authentication) {
        if (addressId == null || authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();
        if (email == null || email.isBlank()) {
            return false;
        }

        return addressRepository.findById(addressId)
                .map(Address::getUser)
                .map(User::getEmail)
                .map(ownerEmail -> ownerEmail.equalsIgnoreCase(email))
                .orElse(false);
    }
}