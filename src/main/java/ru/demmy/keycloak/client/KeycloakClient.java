package ru.demmy.keycloak.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.demmy.keycloak.enums.CommonConst;
import ru.demmy.keycloak.enums.ErrorCode;
import ru.demmy.keycloak.enums.RealmRole;
import ru.demmy.keycloak.exception.AuthenticationException;
import ru.demmy.keycloak.model.UserModel;
import ru.demmy.keycloak.security.JwtParser;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class KeycloakClient {

    private final Keycloak adminClient;
    private final JwtParser jwtParser;

    @Value("${keycloak.url}") private String keycloakUrl;
    @Value("${keycloak.realm}") private String realm;
    @Value("${keycloak.client-id}") private String clientId;

    public Optional<UserRepresentation> findUser(String username) {
        RealmResource realmResource = adminClient.realms().realm(realm);
        List<UserRepresentation> searchResult = realmResource
                .users()
                .searchByUsername(username, true);

        if (searchResult == null || searchResult.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(searchResult.get(0));
    }

    public void logout(String userId) {
        UserResource userResource = getUserResource(userId);
        userResource.logout();
    }

    public UserModel login(String username, String password) throws AuthenticationException {
        log.debug("Login {}", username);
        Keycloak userClient = KeycloakBuilder.builder()
                .serverUrl(keycloakUrl)
                .realm(realm)
                .username(username)
                .password(password)
                .clientId(clientId)
                .build();

        String accessToken = null;
        try {
            accessToken = userClient.tokenManager().getAccessTokenString();
        } catch (NotAuthorizedException ex) {
            log.error(ex.getMessage());
            throw new AuthenticationException(ErrorCode.USER_CREDENTIALS_IS_INVALID);
        }
        return jwtParser.parseJwtIntoUserModel(accessToken);
    }

    public void changeDeviceId(String userId, String newDeviceId) {
        log.debug("Start change device procedure for user {}", userId);
        UserResource userResource = getUserResource(userId);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.singleAttribute(CommonConst.DEVICE_ID, newDeviceId);
        userResource.update(userRepresentation);
    }

    public void changePassword(String userId, String newPassword) {
        log.debug("Start change password procedure for user {}", userId);
        UserResource userResource = getUserResource(userId);
        UserRepresentation userRepresentation = userResource.toRepresentation();

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(newPassword);

        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        userResource.update(userRepresentation);
    }

    public void confirmUserEmail(String userId) {
        log.debug("Start confirm email procedure for user {}", userId);
        UserResource userResource = getUserResource(userId);

        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setEmailVerified(true);
        userResource.update(userRepresentation);
    }

    public String createDeviceUser(String deviceId) throws AuthenticationException {
        log.debug("Creating new device user. DeviceId {}.", deviceId);
        UserRepresentation userRepresentation = createUserRepresentation(null, deviceId, deviceId, deviceId);
        return createUser(userRepresentation, RealmRole.DEVICE);
    }

    public String createDefaultUser(String email, String password, String deviceId) throws AuthenticationException {
        log.debug("Creating new user. Email {}, deviceId {}.", email, deviceId);
        UserRepresentation userRepresentation = createUserRepresentation(email, email, password, deviceId);
        return createUser(userRepresentation, RealmRole.USER);
    }

    private UserResource getUserResource(String userId) {
        RealmResource realmResource = adminClient.realms().realm(realm);
        return realmResource.users().get(userId);
    }

    private String createUser(UserRepresentation userRepresentation, RealmRole realmRole) throws AuthenticationException {
        RealmResource realmResource = adminClient.realms().realm(realm);
        UsersResource usersResource = realmResource.users();
        Response response = usersResource.create(userRepresentation);
        String userId = getCreatedId(response);

        RoleRepresentation roleRepresentation = realmResource.roles()
                .get(realmRole.getName()).toRepresentation();
        realmResource.users().get(userId).roles().realmLevel()
                .add(List.of(roleRepresentation));

        return userId;
    }

    private UserRepresentation createUserRepresentation(String email,
                                                        String username,
                                                        String password,
                                                        String deviceId) {

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);
        if (email != null) {
            userRepresentation.setEmail(email);
            userRepresentation.setEmailVerified(false);
        }
        userRepresentation.setUsername(username);
        userRepresentation.singleAttribute(CommonConst.DEVICE_ID, deviceId);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        return userRepresentation;
    }

    private String getCreatedId(Response response) throws AuthenticationException {
        URI location = response.getLocation();
        if (!response.getStatusInfo().equals(Response.Status.CREATED)) {
            Response.StatusType statusInfo = response.getStatusInfo();
            log.error("Create method returned status {} (Code: {}); expected status: Created (201)",
                    statusInfo.getReasonPhrase(), statusInfo.getStatusCode());
            throw new AuthenticationException(ErrorCode.KEYCLOAK_CREATE_USER_ERROR);
        }
        if (location == null) {
            return null;
        }
        String path = location.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }
}
