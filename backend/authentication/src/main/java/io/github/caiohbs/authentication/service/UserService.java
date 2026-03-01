package io.github.caiohbs.authentication.service;

import io.github.caiohbs.authentication.dto.CreateUserDTO;
import io.github.caiohbs.authentication.dto.ReadUserDTO;
import io.github.caiohbs.authentication.dto.UpdateUserDTO;
import io.github.caiohbs.authentication.dto.mapper.UserDTOMapper;
import io.github.caiohbs.authentication.exception.InvalidPasswordException;
import io.github.caiohbs.authentication.exception.NonNumericException;
import io.github.caiohbs.authentication.exception.ResourceNotFoundException;
import io.github.caiohbs.authentication.exception.UniqueValueException;
import io.github.caiohbs.authentication.model.Address;
import io.github.caiohbs.authentication.model.User;
import io.github.caiohbs.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDTOMapper userDTOMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ReadUserDTO create(CreateUserDTO createUserDTO) {
        String passwordError = passwordCheck(
                createUserDTO.email(), createUserDTO.password(), createUserDTO.fullName(), createUserDTO.birthday()
        );
        if (passwordError != null) {
            throw new InvalidPasswordException(passwordError);
        }

        Address newAddress = new Address(
                createUserDTO.address().street(),
                makeNumeric(createUserDTO.address().number()),
                makeNumeric(createUserDTO.address().zipCode()),
                createUserDTO.address().city(),
                createUserDTO.address().state(),
                createUserDTO.address().mainAddress()
        );

        User newUser = new User(
                createUserDTO.email(),
                passwordEncoder.encode(createUserDTO.password()),
                createUserDTO.fullName(),
                createUserDTO.birthday(),
                makeNumeric(createUserDTO.uniqueLocalIdentification()),
                makeNumeric(createUserDTO.phoneNumber()),
                List.of(newAddress)
        );

        if (!checkEmailAndLocalIdentifierBeforeSaving(newUser)) {
            throw new UniqueValueException("Email and Local Identification must be unique.");
        }

        newAddress.setUser(newUser);
        User savedUser = userRepository.save(newUser);

        return userDTOMapper.apply(savedUser);
    }

    public List<ReadUserDTO> getAll() {
        return userRepository.findAll().stream().map(userDTOMapper).collect(Collectors.toList());
    }

    public ReadUserDTO getUserById(Long id) {
        User foundUser = getUserByIdConvertingOptional(id);
        return userDTOMapper.apply(foundUser);
    }

    public ReadUserDTO updateUser(UpdateUserDTO updateUserDTO, Long id) {
        User foundUser = getUserByIdConvertingOptional(id);

        String passwordError = passwordCheck(
                updateUserDTO.newEmail(), passwordEncoder.encode(updateUserDTO.newPassword()),
                foundUser.getFullName(), foundUser.getBirthday()
        );
        if (passwordError != null) {
            throw new InvalidPasswordException(passwordError);
        }

        foundUser.setEmail(updateUserDTO.newEmail());
        foundUser.setPassword(updateUserDTO.newPassword());
        foundUser.setPhoneNumber(updateUserDTO.phoneNumber());

        if (!checkEmailBeforeSaving(foundUser)) {
            throw new UniqueValueException("Email must be unique.");
        }

        User updatedUser = userRepository.save(foundUser);

        return userDTOMapper.apply(updatedUser);
    }

    public void deleteUserById(Long id) {
        User foundUser = getUserByIdConvertingOptional(id);

        foundUser.setActive(false);
        userRepository.save(foundUser);
    }

    public User getUserByIdConvertingOptional(Long id) {
        Optional<User> findUser = userRepository.findById(id);

        if (findUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found for id: " + id);
        }

        return findUser.get();
    }

    public User getUserByEmailConvertingOptional(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found for email: " + email);
        }

        return findUser.get();
    }

    public String passwordCheck(String email, String password, String fullName, LocalDate birthday) {
        List<String> dateFormats = new ArrayList<>();

        String date1 = birthday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String date2 = birthday.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        dateFormats.add(birthday.toString());
        dateFormats.add(date1);
        dateFormats.add(date2);
        dateFormats.add(makeNumeric(date1));
        dateFormats.add(makeNumeric(date2));

        for (String dateFormat : dateFormats) {
            if (password.contains(dateFormat)) {
                return "Password can't contain user's birthday";
            }
        }

        if (password.contains(email)) {
            return "Password can't contain user email";
        }

        String[] individualNames = fullName.split("\\s+");
        for (String name : individualNames) {
            if (password.contains(name)) {
                return "Password can't contain user's name";
            }
        }
        return null;
    }

    private String makeNumeric(String value) {
        value = value.replaceAll("[^0-9]", "");

        if (!value.matches("-?\\d+(\\.\\d+)?")) {
            throw new NonNumericException("This value is expected to be numeric: '" + value + "'");
        }

        return value;
    }

    private boolean checkEmailBeforeSaving(User user) {
        return userRepository.findByEmail(user.getEmail()).isEmpty();
    }

    private boolean checkEmailAndLocalIdentifierBeforeSaving(User user) {
        boolean emailExists = userRepository.findByEmail(user.getEmail()).isPresent();

        boolean localIdentifierExists = userRepository
                .findByUniqueLocalIdentification(user.getUniqueLocalIdentification()).isPresent();

        return !emailExists && !localIdentifierExists;
    }

}
