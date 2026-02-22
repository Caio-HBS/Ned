package io.github.caiohbs.authentication.dto.mapper;

import io.github.caiohbs.authentication.dto.ReadAddressDTO;
import io.github.caiohbs.authentication.model.Address;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AddressDTOMapper implements Function<Address, ReadAddressDTO> {

    @Override
    public ReadAddressDTO apply(Address address) {
        return new ReadAddressDTO(
                address.getAddressId(),
                address.getStreet(),
                address.getNumber(),
                address.getZipCode(),
                address.getCity(),
                address.getState(),
                address.isMainAddress()
        );
    }

}
