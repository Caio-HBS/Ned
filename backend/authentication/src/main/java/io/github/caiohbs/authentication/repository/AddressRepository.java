package io.github.caiohbs.authentication.repository;

import io.github.caiohbs.authentication.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByAddressId(Long addressId);
    Optional<Address> findByStreetAndNumberAndZipCodeAndCityAndState(
            String street, String number, String zipCode, String city, String state
    );
}
