package ru.demmy.keycloak.util;

import org.springframework.security.core.context.SecurityContextHolder;
import ru.demmy.keycloak.enums.ErrorCode;
import ru.demmy.keycloak.exception.AuthenticationException;
import ru.demmy.keycloak.model.UserModel;

public class SecurityContextUtil {

    public static UserModel getUser() throws AuthenticationException {
        UserModel userModel = getUserOrNull();
        if (userModel != null) {
            return userModel;
        }
        throw new AuthenticationException(ErrorCode.TOKEN_WITHOUT_USER_MODEL);
    }

    public static UserModel getUserOrNull() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserModel userModel) {
            return userModel;
        }
        return null;
    }
}
