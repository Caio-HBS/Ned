package io.github.caiohbs.authentication.service;

import io.github.caiohbs.authentication.dto.CreateAddressDTO;
import io.github.caiohbs.authentication.dto.ReadAddressDTO;
import io.github.caiohbs.authentication.dto.UpdateAddressDTO;
import io.github.caiohbs.authentication.exception.ResourceNotFoundException;
import io.github.caiohbs.authentication.model.Address;
import io.github.caiohbs.authentication.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public ReadAddressDTO createAddress(CreateAddressDTO createAddressDTO) {
        // TODO:
        //  check uniqueness of the address.
        return null;
    }

    public List<ReadAddressDTO> getAllAddresses() {
        return null;
    }

    public List<ReadAddressDTO> getAllAddressesByUser(Long userId) {
        // TODO:
        //  check if user exists.
        return null;
    }

    public ReadAddressDTO getSingleAddress(Long addressId) {
        return null;
    }

    public ReadAddressDTO updateAddress(UpdateAddressDTO updateAddressDTO, Long addressId) {
        // TODO:
        //  check uniqueness of the address relative to the user
        //  getAddressById(addressId)
        return null;
    }

    public void deleteAddress(Long addressId) {
        Address foundAddress = getAddressOptionalById(addressId);

        foundAddress.setActive(false);
        addressRepository.save(foundAddress);
    }

    public Address getAddressOptionalById(Long addressId) {
        Optional<Address> foundAddress = addressRepository.findById(addressId);

        if (foundAddress.isEmpty()) {
            throw new ResourceNotFoundException("Address not found for id: " + addressId);
        }

        return foundAddress.get();
    }

}
