package ru.demmy.keycloak.model;

import lombok.Builder;
import lombok.Data;
import ru.demmy.keycloak.enums.RealmRole;

import java.util.Set;

@Data
@Builder
public class UserModel {

    private String userId;
    private String username;
    private String email;
    private Boolean emailVerified;
    private String deviceId;
    private Set<RealmRole> realmRoles;
    private String accessToken;
}
