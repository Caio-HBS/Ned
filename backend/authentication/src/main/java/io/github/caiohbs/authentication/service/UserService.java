package io.github.caiohbs.authentication.service;

import io.github.caiohbs.authentication.dto.CreateUserDTO;
import io.github.caiohbs.authentication.dto.ReadUserDTO;
import io.github.caiohbs.authentication.dto.UpdateUserDTO;
import io.github.caiohbs.authentication.dto.mapper.UserDTOMapper;
import io.github.caiohbs.authentication.exception.InvalidPasswordException;
import io.github.caiohbs.authentication.exception.ResourceNotFoundException;
import io.github.caiohbs.authentication.exception.UniqueValueException;
import io.github.caiohbs.authentication.model.Address;
import io.github.caiohbs.authentication.model.GenericEmail;
import io.github.caiohbs.authentication.model.User;
import io.github.caiohbs.authentication.model.UserToken;
import io.github.caiohbs.authentication.model.enums.EmailActionType;
import io.github.caiohbs.authentication.model.enums.UserTokenType;
import io.github.caiohbs.authentication.publisher.SendEmailPublisher;
import io.github.caiohbs.authentication.repository.UserRepository;
import io.github.caiohbs.authentication.util.RequestContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserTokenService userTokenService;
    private final UserDTOMapper userDTOMapper;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidationService passwordValidationService;
    private final SendEmailPublisher sendEmailPublisher;

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

        String token = userTokenService.create(UserTokenType.EMAIL_VERIFICATION, savedUser, 24);
        GenericEmail activateAccountEmail = new GenericEmail(
                savedUser.getUserId(), savedUser.getEmail(), savedUser.getFullName(),
                token, RequestContextUtil.getRequestContent(), EmailActionType.ACCOUNT_ACTIVATION_REQUEST
        );
        sendEmailPublisher.sendEmail(activateAccountEmail);

        return userDTOMapper.apply(savedUser);
    }

    public Page<ReadUserDTO> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userDTOMapper);
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

        GenericEmail updatedAccountEmail = new GenericEmail(
                updatedUser.getUserId(), updatedUser.getEmail(), updatedUser.getFullName(),
                null, RequestContextUtil.getRequestContent(), EmailActionType.UPDATED_ACCOUNT
        );
        sendEmailPublisher.sendEmail(updatedAccountEmail);

        return userDTOMapper.apply(updatedUser);
    }

    @Transactional
    public void deleteUserById(Long id) {
        User foundUser = getUserByIdConvertingOptional(id);

        foundUser.setActive(false);

        GenericEmail deletedAccountEmail = new GenericEmail(
                foundUser.getUserId(), foundUser.getEmail(), foundUser.getFullName(), null,
                RequestContextUtil.getRequestContent(), EmailActionType.ACCOUNT_DEACTIVATION
        );
        sendEmailPublisher.sendEmail(deletedAccountEmail);

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
