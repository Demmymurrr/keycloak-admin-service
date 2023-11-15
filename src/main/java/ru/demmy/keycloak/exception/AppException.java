package ru.demmy.keycloak.exception;

import lombok.Getter;

@Getter
public abstract class AppException extends Exception{

    private final String code;
    private final String message;

    protected AppException(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
