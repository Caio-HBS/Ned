package io.github.caiohbs.authentication.service;

import io.github.caiohbs.authentication.dto.ReadUserDTO;
import io.github.caiohbs.authentication.dto.mapper.UserDTOMapper;
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

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTokenService {

    private final UserTokenRepository userTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDTOMapper userDTOMapper;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public UserToken create(UserTokenType tokenType, User user) {
        validateTokenType(tokenType);
        
        String generatedToken = generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);
        
        UserToken userToken = new UserToken(tokenType, passwordEncoder.encode(generatedToken), expiresAt);
        userToken.setUser(user);
        
        return userTokenRepository.save(userToken);
    }

    private void validateTokenType(UserTokenType tokenType) {

        List<UserTokenType> validTokens = List.of(UserTokenType.values());

        if (tokenType == null || !validTokens.contains(tokenType)) {
            throw new UserTokenException("Token is invalid.");
        }

    }

    public ReadUserDTO consumeToken(UserTokenType tokenType, String token) {
        UserToken checkToken = getUserToken(token);
        User foundUser = userRepository.findById(checkToken.getUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found for token"));
        
        return consumeTokenInternal(tokenType, checkToken, foundUser);
    }

    private ReadUserDTO consumeTokenInternal(UserTokenType tokenType, UserToken checkToken, User foundUser) {

        User updatedUser = null;

        switch (tokenType) {
            case EMAIL_VERIFICATION:
                checkToken.setUsedAt(LocalDateTime.now());
                checkToken.setActive(false);

                foundUser.setAccountNonLocked(true);

                userTokenRepository.save(checkToken);
                updatedUser = userRepository.save(foundUser);
                break;
            case RESET_EMAIL:
                //TODO: Implement
                break;
            default:
                throw new UserTokenException(tokenType + " is not a valid token type.");
        }

        if (updatedUser != null) {
            return userDTOMapper.apply(updatedUser);
        }
        throw new UserTokenException("Error processing token.");

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
