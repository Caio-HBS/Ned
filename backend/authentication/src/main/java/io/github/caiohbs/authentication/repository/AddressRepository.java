package io.github.caiohbs.authentication.repository;

import io.github.caiohbs.authentication.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
