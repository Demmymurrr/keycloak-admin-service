package ru.demmy.keycloak.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.demmy.keycloak.enums.RealmRole;
import ru.demmy.keycloak.protocol.response.LoginResponse;
import ru.demmy.keycloak.exception.AuthenticationException;
import ru.demmy.keycloak.model.UserModel;
import ru.demmy.keycloak.protocol.request.LoginRequest;
import ru.demmy.keycloak.util.SecurityContextUtil;

@Log4j2
@Service
@RequiredArgsConstructor
public class LoginService {


    public LoginResponse login(LoginRequest loginRequest) throws AuthenticationException {

        UserModel userModel = SecurityContextUtil.getUser();
        if (!userModel.getRealmRoles().contains(RealmRole.ADMIN)) {
            // attest
        }

        return LoginResponse.builder()
                .userId(userModel.getUserId())
                .accessToken(userModel.getAccessToken())
                .build();
    }

}
