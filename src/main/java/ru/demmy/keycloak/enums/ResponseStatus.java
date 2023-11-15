package ru.demmy.keycloak.enums;

import lombok.Getter;

@Getter
public enum ResponseStatus {

    SUCCESS,
    ERROR,
    WAIT,
    CHECK_UNCONFIRMED;

}
