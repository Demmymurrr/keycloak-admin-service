package ru.demmy.keycloak.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RealmRole {

    DEVICE("device_user"),
    USER("user"),
    ADMIN("admin"),

    UNKNOWN("any_other_role");

    private final String name;

    public static RealmRole parse(String role) {
        return Arrays.stream(RealmRole.values())
                .filter(realmRole -> realmRole.getName().equals(role))
                .findAny()
                .orElse(UNKNOWN);
    }

}
