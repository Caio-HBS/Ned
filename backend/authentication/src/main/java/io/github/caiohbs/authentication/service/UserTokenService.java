package io.github.caiohbs.authentication.service;

import io.github.caiohbs.authentication.dto.ReadUserDTO;
import io.github.caiohbs.authentication.dto.ResetPasswordDTO;
import io.github.caiohbs.authentication.dto.mapper.UserDTOMapper;
import io.github.caiohbs.authentication.exception.InvalidPasswordException;
import io.github.caiohbs.authentication.exception.ResourceNotFoundException;
import io.github.caiohbs.authentication.exception.UserTokenException;
import io.github.caiohbs.authentication.model.User;
import io.github.caiohbs.authentication.model.UserToken;
import io.github.caiohbs.authentication.model.enums.UserTokenType;
import io.github.caiohbs.authentication.repository.UserRepository;
import io.github.caiohbs.authentication.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserTokenService {

    private final UserTokenRepository userTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDTOMapper userDTOMapper;
    private final PasswordValidationService passwordValidationService;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public UserToken create(UserTokenType tokenType, User user, int hoursToExpire) {
        validateTokenType(tokenType);
        checkActiveTokensForUser(tokenType, user);
        
        String generatedToken = generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(hoursToExpire);
        
        UserToken userToken = new UserToken(tokenType, passwordEncoder.encode(generatedToken), expiresAt);
        userToken.setUser(user);
        
        return userTokenRepository.save(userToken);
    }

    public String createResetPasswordToken(String userEmail) {
        Optional<User> findUser = userRepository.findByEmail(userEmail);

        if (findUser.isPresent()) {
            User foundUser = findUser.get();
            if (!foundUser.isAccountNonLocked()) {
                throw new UserTokenException("Account is locked.");
            }
            // No throwing exception, as to not expose if the user exists or not.
            UserToken token = create(UserTokenType.RESET_PASSWORD, foundUser, 6);
            // TODO: Chamada ao Kafka para passar o token a ser enviado por email.
        }
        return "If the email you provided is registered, you will receive a password reset link shortly.";
    }

    @Transactional
    public ReadUserDTO consumeToken(UserTokenType tokenType, String token, ResetPasswordDTO resetPasswordDTO) {
        UserToken checkToken = getUserToken(token);
        User foundUser = userRepository.findById(checkToken.getUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found for token"));

        return consumeTokenInternal(tokenType, checkToken, foundUser, resetPasswordDTO);
    }

    private ReadUserDTO consumeTokenInternal(
            UserTokenType tokenType, UserToken checkToken, User foundUser, ResetPasswordDTO resetPasswordDTO
    ) {

        User updatedUser = null;

        switch (tokenType) {
            case EMAIL_VERIFICATION:
                checkToken.setUsedAt(LocalDateTime.now());
                checkToken.setActive(false);

                foundUser.setAccountNonLocked(true);

                userTokenRepository.save(checkToken);
                updatedUser = userRepository.save(foundUser);
                // TODO: Chamada ao Kafka para informar a ativação da conta.
                break;
            case RESET_PASSWORD:
                if (resetPasswordDTO == null) {
                    throw new RuntimeException("Reset password DTO is required for this token type.");
                }
                
                String passwordError = passwordValidationService.validatePassword(
                        foundUser.getEmail(), resetPasswordDTO.password(),
                        foundUser.getFullName(), foundUser.getBirthday()
                );
                if (passwordError != null) {
                    throw new InvalidPasswordException(passwordError);
                }

                foundUser.setPassword(passwordEncoder.encode(resetPasswordDTO.password()));
                checkToken.setUsedAt(LocalDateTime.now());
                checkToken.setActive(false);

                userTokenRepository.save(checkToken);
                updatedUser = userRepository.save(foundUser);
                // TODO: Chamada ao Kafka para informar o reset.
                break;
            default:
                throw new UserTokenException(tokenType + " is not a valid token type.");
        }

        return userDTOMapper.apply(updatedUser);

    }

    private void validateTokenType(UserTokenType tokenType) {
        List<UserTokenType> validTokens = List.of(UserTokenType.values());

        if (tokenType == null || !validTokens.contains(tokenType)) {
            throw new UserTokenException("Token is invalid.");
        }
    }

    private void checkActiveTokensForUser(UserTokenType tokenType, User user) {

        Optional<UserToken> findToken = userTokenRepository.findAll().stream()
                .filter(UserToken::isActive)
                .filter(ut -> ut.getUser().equals(user))
                .filter(ut -> ut.getTokenType().equals(tokenType))
                .findFirst();

        if (findToken.isPresent()) {
            throw new UserTokenException("User already has an active token for that action.");
        }

    }


    private String generateToken() {
        byte[] tokenBytes = new byte[32];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private UserToken getUserToken(String token) {
        return userTokenRepository.findAll().stream()
                .filter(UserToken::isActive)
                .filter(ut -> passwordEncoder.matches(token, ut.getToken()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Token not found or expired."));
    }

}
