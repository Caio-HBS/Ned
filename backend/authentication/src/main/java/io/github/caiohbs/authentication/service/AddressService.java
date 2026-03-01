package io.github.caiohbs.authentication.service;

import io.github.caiohbs.authentication.dto.CreateAddressDTO;
import io.github.caiohbs.authentication.dto.ReadAddressDTO;
import io.github.caiohbs.authentication.dto.UpdateAddressDTO;
import io.github.caiohbs.authentication.dto.mapper.AddressDTOMapper;
import io.github.caiohbs.authentication.exception.InvalidOperationException;
import io.github.caiohbs.authentication.exception.ResourceNotFoundException;
import io.github.caiohbs.authentication.exception.UniqueValueException;
import io.github.caiohbs.authentication.model.Address;
import io.github.caiohbs.authentication.model.User;
import io.github.caiohbs.authentication.repository.AddressRepository;
import io.github.caiohbs.authentication.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressDTOMapper addressDTOMapper;

    @Transactional
    public ReadAddressDTO createAddress(CreateAddressDTO createAddressDTO, String email) {
        User findUser = getUserByEmailConvertingOptional(email);

        if (checkAddressBeforeSaving(
                createAddressDTO.street(), createAddressDTO.number(), createAddressDTO.zipCode(),
                createAddressDTO.city(), createAddressDTO.state())
        ) {
            throw new UniqueValueException("This address already exists in our database.");
        }

        if (createAddressDTO.mainAddress()) {
            makeAllAddressesNotMain(findUser);
        }

        Address newAddress = new Address(
                createAddressDTO.street(),
                createAddressDTO.number(),
                createAddressDTO.zipCode(),
                createAddressDTO.city(),
                createAddressDTO.state(),
                createAddressDTO.mainAddress()
        );

        Address savedAddress = addressRepository.save(newAddress);
        return addressDTOMapper.apply(savedAddress);
    }


    public List<ReadAddressDTO> getAllAddresses() {
        return addressRepository.findAll().stream().map(addressDTOMapper).collect(Collectors.toList());
    }

    public List<ReadAddressDTO> getAllAddressesByUser(Long userId) {
        User findUser = getUserByIdConvertingOptional(userId);

        List<Address> addresses = findUser.getAddresses();

        return addresses.stream().map(addressDTOMapper).collect(Collectors.toList());
    }

    public ReadAddressDTO getSingleAddress(Long addressId) {
        return addressDTOMapper.apply(getAddressByIdConvertingOptional(addressId));
    }

    @Transactional
    public ReadAddressDTO updateAddress(UpdateAddressDTO updateAddressDTO, Long addressId) {
        Address foundAddress = getAddressByIdConvertingOptional(addressId);
        User addressOwner = foundAddress.getUser();

        if (checkAddressBeforeSaving(
                updateAddressDTO.street(), updateAddressDTO.number(), updateAddressDTO.zipCode(),
                updateAddressDTO.city(), updateAddressDTO.state()
        )) {
            throw new UniqueValueException("This address already exists in our database.");
        }

        if (updateAddressDTO.mainAddress()) {
            makeAllAddressesNotMain(addressOwner);
        }

        foundAddress.setStreet(updateAddressDTO.street());
        foundAddress.setNumber(updateAddressDTO.number());
        foundAddress.setZipCode(updateAddressDTO.zipCode());
        foundAddress.setCity(updateAddressDTO.city());
        foundAddress.setState(updateAddressDTO.state());
        foundAddress.setMainAddress(updateAddressDTO.mainAddress());

        Address updatedAddress = addressRepository.save(foundAddress);
        return addressDTOMapper.apply(updatedAddress);
    }

    @Transactional
    public void deleteAddress(Long addressId) {
        Address addressToBeDeleted = getAddressByIdConvertingOptional(addressId);
        List<Address> listOfAddresses = getAddressesFromUser(addressToBeDeleted);

        if (addressToBeDeleted.isMainAddress()) {
            Address newMain = listOfAddresses.stream()
                    .filter(a -> !a.getAddressId().equals(addressToBeDeleted.getAddressId()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidOperationException("Could not select a new main address."));

            newMain.setMainAddress(true);
            addressRepository.save(newMain);
        }

        addressToBeDeleted.setActive(false);
        addressRepository.save(addressToBeDeleted);
    }

    private List<Address> getAddressesFromUser(Address addressToBeDeleted) {
        User addressOwner = addressToBeDeleted.getUser();
        if (addressOwner == null) {
            throw new InvalidOperationException("Address has no owner user.");
        }

        List<Address> listOfAddresses = addressOwner.getAddresses();
        if (listOfAddresses == null || listOfAddresses.isEmpty()) {
            throw new InvalidOperationException("User has no addresses to manage.");
        }

        if (listOfAddresses.size() == 1) {
            throw new InvalidOperationException("You can't delete your only address.");
        }
        return listOfAddresses;
    }

    public Address getAddressByIdConvertingOptional(Long addressId) {
        Optional<Address> foundAddress = addressRepository.findById(addressId);

        if (foundAddress.isEmpty()) {
            throw new ResourceNotFoundException("Address not found for id: " + addressId);
        }

        return foundAddress.get();
    }

    private User getUserByIdConvertingOptional(Long userId) {
        Optional<User> findUser = userRepository.findById(userId);

        if (findUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found for id: " + userId);
        }
        return findUser.get();
    }

    private User getUserByEmailConvertingOptional(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found for email: " + email);
        }
        return findUser.get();
    }

    private void makeAllAddressesNotMain(User findUser) {
        List<Address> addresses = findUser.getAddresses();
        for (Address address : addresses) {
            address.setMainAddress(false);
            addressRepository.save(address);
        }
    }

    private boolean checkAddressBeforeSaving(String street, String number, String zipCode, String city, String state) {
        Optional<Address> addressExistsForUser = addressRepository.findByStreetAndNumberAndZipCodeAndCityAndState(
                street, number, zipCode, city, state
        );

        return addressExistsForUser.isPresent();
    }

}
