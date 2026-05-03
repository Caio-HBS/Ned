package io.github.caiohbs.authentication.service;

import io.github.caiohbs.authentication.dto.TokenResponseDTO;
import io.github.caiohbs.authentication.model.GenericEmail;
import io.github.caiohbs.authentication.model.User;
import io.github.caiohbs.authentication.model.enums.EmailActionType;
import io.github.caiohbs.authentication.publisher.SendEmailPublisher;
import io.github.caiohbs.authentication.util.RequestContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final SendEmailPublisher sendEmailPublisher;

    public TokenResponseDTO login(String username, String password) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        User foundUser = userService.getUserByEmailConvertingOptional(username);
        GenericEmail updatedAccountEmail = new GenericEmail(
                foundUser.getUserId(), foundUser.getEmail(), foundUser.getFullName(),
                null, LocalDateTime.now(), EmailActionType.NEW_LOGIN, RequestContextUtil.getRequestContent()
        );
        sendEmailPublisher.sendEmail(updatedAccountEmail);

        return new TokenResponseDTO(tokenService.generateToken(authentication.getName()));
    }

    public TokenResponseDTO refresh(String authorizationHeader) {
        String newToken = tokenService.refreshFromAuthorizationHeader(authorizationHeader);
        return new TokenResponseDTO(newToken);
    }

}
