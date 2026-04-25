package io.github.caiohbs.authentication.service;

import io.github.caiohbs.authentication.dto.CreateUserDTO;
import io.github.caiohbs.authentication.dto.ReadUserDTO;
import io.github.caiohbs.authentication.dto.UpdateUserDTO;
import io.github.caiohbs.authentication.dto.mapper.UserDTOMapper;
import io.github.caiohbs.authentication.exception.InvalidPasswordException;
import io.github.caiohbs.authentication.exception.ResourceNotFoundException;
import io.github.caiohbs.authentication.exception.UniqueValueException;
import io.github.caiohbs.authentication.model.Address;
import io.github.caiohbs.authentication.model.User;
import io.github.caiohbs.authentication.model.enums.UserTokenType;
import io.github.caiohbs.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserTokenService userTokenService;
    private final UserDTOMapper userDTOMapper;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidationService passwordValidationService;

    @Transactional
    public ReadUserDTO create(CreateUserDTO createUserDTO) {
        String passwordError = passwordValidationService.validatePassword(
                createUserDTO.email(), createUserDTO.password(), createUserDTO.fullName(), createUserDTO.birthday()
        );
        if (passwordError != null) {
            throw new InvalidPasswordException(passwordError);
        }

        Address newAddress = new Address(
                createUserDTO.address().street(),
                passwordValidationService.makeNumeric(createUserDTO.address().number()),
                passwordValidationService.makeNumeric(createUserDTO.address().zipCode()),
                createUserDTO.address().city(),
                createUserDTO.address().state(),
                createUserDTO.address().mainAddress()
        );

        User newUser = new User(
                createUserDTO.email(),
                passwordEncoder.encode(createUserDTO.password()),
                createUserDTO.fullName(),
                createUserDTO.birthday(),
                passwordValidationService.makeNumeric(createUserDTO.uniqueLocalIdentification()),
                passwordValidationService.makeNumeric(createUserDTO.phoneNumber()),
                List.of(newAddress)
        );

        if (!checkEmailAndLocalIdentifierBeforeSaving(newUser)) {
            throw new UniqueValueException("Email and Local Identification must be unique.");
        }

        newAddress.setUser(newUser);
        User savedUser = userRepository.save(newUser);

        userTokenService.create(UserTokenType.EMAIL_VERIFICATION, savedUser, 24);
        // TODO: Chamada ao Kafka para passar o token a ser enviado por email.

        return userDTOMapper.apply(savedUser);
    }

    public List<ReadUserDTO> getAll() {
        return userRepository.findAll().stream().map(userDTOMapper).collect(Collectors.toList());
    }

    public ReadUserDTO getUserById(Long id) {
        User foundUser = getUserByIdConvertingOptional(id);
        return userDTOMapper.apply(foundUser);
    }

    @Transactional
    public ReadUserDTO updateUser(UpdateUserDTO updateUserDTO, Long id) {
        User foundUser = getUserByIdConvertingOptional(id);

        String passwordError = passwordValidationService.validatePassword(
                updateUserDTO.newEmail(), updateUserDTO.newPassword(),
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
        // TODO: Chamada ao Kafka para informar a alteração na conta.

        return userDTOMapper.apply(updatedUser);
    }

    @Transactional
    public void deleteUserById(Long id) {
        User foundUser = getUserByIdConvertingOptional(id);

        foundUser.setActive(false);
        // TODO: Chamada ao Kafka para informar a desativação da conta.
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
