package ru.demmy.keycloak.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.demmy.keycloak.client.KeycloakClient;
import ru.demmy.keycloak.exception.AuthenticationException;
import ru.demmy.keycloak.model.UserModel;
import ru.demmy.keycloak.protocol.request.ChangePasswordRequest;

@Log4j2
@Service
public record UserService(KeycloakClient keycloakClient) {

    public void handleChangePasswordRequest(ChangePasswordRequest request) throws AuthenticationException {
        UserModel userModel = keycloakClient.login(request.getEmail(), request.getPasswordHash());
        changePassword(userModel.getUserId(), userModel.getEmail(), request.getNewPasswordHash());
    }


    public void changePassword(String userId, String userEmail, String newPassword) {
        keycloakClient.changePassword(userId, newPassword);
        keycloakClient.logout(userId);
    }


}
