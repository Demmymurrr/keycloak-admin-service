package ru.demmy.keycloak.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import ru.demmy.keycloak.enums.ErrorCode;
import ru.demmy.keycloak.protocol.request.RegisterRequest;
import ru.demmy.keycloak.protocol.response.RegisterResponse;
import ru.demmy.keycloak.client.KeycloakClient;
import ru.demmy.keycloak.exception.AuthenticationException;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class RegisterService {

    private final KeycloakClient keycloakClient;

    public RegisterResponse register(RegisterRequest registerRequest) throws AuthenticationException {
        Optional<UserRepresentation> userRepresentation = keycloakClient.findUser(registerRequest.getEmail());

        if (userRepresentation.isPresent()) {
            log.debug("User with email {} already exist", registerRequest.getEmail());
            throw new AuthenticationException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }

        String defaultUserId = keycloakClient.createDefaultUser(
                registerRequest.getEmail(),
                registerRequest.getPasswordHash(),
                registerRequest.getDeviceId()
        );

        keycloakClient.confirmUserEmail(defaultUserId);
        return RegisterResponse.builder()
                .userId(defaultUserId)
                .confirmationSent(true)
                .build();
    }

}
