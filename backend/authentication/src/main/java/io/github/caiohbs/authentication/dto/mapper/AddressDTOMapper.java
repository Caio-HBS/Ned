package io.github.caiohbs.authentication.dto.mapper;

import io.github.caiohbs.authentication.dto.CreateOrReadAddressDTO;
import io.github.caiohbs.authentication.model.Address;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AddressDTOMapper implements Function<Address, CreateOrReadAddressDTO> {

    @Override
    public CreateOrReadAddressDTO apply(Address address) {
        return new CreateOrReadAddressDTO(
                address.getStreet(),
                address.getNumber(),
                address.getZipCode(),
                address.getCity(),
                address.getState(),
                address.isMainAddress()
        );
    }

}
