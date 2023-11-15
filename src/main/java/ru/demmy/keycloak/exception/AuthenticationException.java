package ru.demmy.keycloak.exception;

import lombok.Getter;
import ru.demmy.keycloak.enums.ErrorCode;

@Getter
public class AuthenticationException extends AppException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getMessage());
    }
}
