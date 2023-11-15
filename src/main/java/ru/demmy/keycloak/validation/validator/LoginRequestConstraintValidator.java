package ru.demmy.keycloak.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import ru.demmy.keycloak.enums.ErrorCode;
import ru.demmy.keycloak.model.UserModel;
import ru.demmy.keycloak.protocol.request.LoginRequest;
import ru.demmy.keycloak.client.KeycloakClient;
import ru.demmy.keycloak.exception.AuthenticationException;
import ru.demmy.keycloak.security.KeycloakAuthenticationConverter;
import ru.demmy.keycloak.validation.constraint.LoginRequestValidator;

import java.util.Optional;

@Log4j2
@Component
public record LoginRequestConstraintValidator(KeycloakAuthenticationConverter converter,
                                              JwtDecoder jwtDecoder,
                                              KeycloakClient keycloakClient)
        implements ConstraintValidator<LoginRequestValidator, LoginRequest> {

    @Override
    public void initialize(LoginRequestValidator constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @SneakyThrows
    @Override
    public boolean isValid(LoginRequest request, ConstraintValidatorContext context) {
        UserModel userModel = authenticate(request);
        verifyUserModel(userModel);
        verifyUserDevice(userModel, request.getDeviceId());

        Authentication authentication = converter
                .convert(jwtDecoder.decode(userModel.getAccessToken()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return true;
    }

    private UserModel authenticate(LoginRequest loginRequest) throws AuthenticationException {
        if (loginRequest.isDeviceLogin()) {
            Optional<UserRepresentation> deviceUserOptional = keycloakClient.findUser(loginRequest.getDeviceId());
            if (deviceUserOptional.isEmpty()) {
                keycloakClient.createDeviceUser(loginRequest.getDeviceId());
            }
            return keycloakClient.login(loginRequest.getDeviceId(), loginRequest.getDeviceId());
        } else {
            return keycloakClient.login(loginRequest.getEmail(), loginRequest.getPasswordHash());
        }
    }

    private void verifyUserDevice(UserModel userModel, String deviceId) throws AuthenticationException {
        if (!userModel.getDeviceId().equals(deviceId)) {
            throw new AuthenticationException(ErrorCode.USER_CREDENTIALS_IS_INVALID);
        }
    }

    private void verifyUserModel(UserModel userModel) throws AuthenticationException {
        if (!userModel.getEmailVerified()) {
            throw new AuthenticationException(ErrorCode.EMAIL_IS_NOT_VERIFIED);
        }
    }
}
