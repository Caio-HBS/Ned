package io.github.caiohbs.authentication.dto.mapper;

import io.github.caiohbs.authentication.dto.ReadUserDTO;
import io.github.caiohbs.authentication.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDTOMapper implements Function<User, ReadUserDTO> {

    private final AddressDTOMapper AddressDTOMapper;

    @Override
    public ReadUserDTO apply(User user) {
        return new ReadUserDTO(
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                user.getBirthday(),
                user.getUniqueLocalIdentification(),
                user.getPhoneNumber(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getAddresses().stream().map(AddressDTOMapper).collect(Collectors.toList())
        );
    }

}
