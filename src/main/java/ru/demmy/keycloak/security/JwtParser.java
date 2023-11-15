package ru.demmy.keycloak.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ru.demmy.keycloak.enums.CommonConst;
import ru.demmy.keycloak.enums.ErrorCode;
import ru.demmy.keycloak.enums.RealmRole;
import ru.demmy.keycloak.exception.AuthenticationException;
import ru.demmy.keycloak.model.UserModel;

import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public record JwtParser(ObjectMapper objectMapper) {

    public Map<String, Object> parseJwtPayload(String jwt) throws AuthenticationException {
        try {
            String[] split_string = jwt.split("\\.");
            String base64EncodedBody = split_string[1];
            Base64.Decoder base64Url = Base64.getDecoder();
            String body = new String(base64Url.decode(base64EncodedBody));

            return objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new AuthenticationException(ErrorCode.TOKEN_PAYLOAD_DECRYPTION_ERROR);
        }
    }

    public UserModel parseJwtIntoUserModel(String jwt) throws AuthenticationException {
        Map<String, Object> payloadMap = parseJwtPayload(jwt);
        if (payloadMap == null || payloadMap.isEmpty()) {
            throw new AuthenticationException(ErrorCode.TOKEN_PAYLOAD_DECRYPTION_ERROR);
        }
        Set<RealmRole> roleSet = extractRealmRoles(payloadMap);
        return UserModel.builder()
                .userId((String) payloadMap.get(CommonConst.SUBJECT))
                .username((String) payloadMap.get(CommonConst.USERNAME))
                .email((String) payloadMap.get(CommonConst.EMAIL))
                .emailVerified((Boolean) payloadMap.get(CommonConst.EMAIL_VERIFIED))
                .deviceId((String) payloadMap.get(CommonConst.DEVICE_ID))
                .realmRoles(roleSet)
                .accessToken(jwt)
                .build();
    }

    private Set<RealmRole> extractRealmRoles(Map<String, Object> payloadMap) throws AuthenticationException {
        Object realmAccessPayload = payloadMap.get(CommonConst.REALM_ACCESS);
        if (realmAccessPayload == null) {
            throw new AuthenticationException(ErrorCode.TOKEN_PAYLOAD_REALM_ACCESS_ERROR);
        }

        HashMap<String, List<String>> realmAccessMap =
                objectMapper.convertValue(realmAccessPayload, new TypeReference<HashMap<String, List<String>>>() {});
        List<String> roleStringList = realmAccessMap.get(CommonConst.ROLES);
        Set<RealmRole> roleSet = new HashSet<>();
        for (String roleString : roleStringList) {
            RealmRole realmRole = RealmRole.parse(roleString);
            roleSet.add(realmRole);
        }
        return roleSet;
    }
}
