package ru.demmy.keycloak.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /**
     * AuthenticationException
     */
    KEYCLOAK_UNAVAILABLE("1000", "Authentication management service is unavailable."),
    KEYCLOAK_CREATE_USER_ERROR("1001", "Error while user creation."),
    EMAIL_ALREADY_REGISTERED("1002", "Email is already registered."),
    EMAIL_IS_NOT_VERIFIED("1004","Email is not verified."),
    USER_CREDENTIALS_IS_INVALID("1005","User credentials is invalid."),

    TOKEN_WITHOUT_USER_MODEL("1101","Token principal doesn't contains UserModel."),
    TOKEN_PAYLOAD_DECRYPTION_ERROR("1102", "Token decryption error."),
    TOKEN_PAYLOAD_REALM_ACCESS_ERROR("1103", "Realm access is null.");


    private final String code;
    private final String message;
}
