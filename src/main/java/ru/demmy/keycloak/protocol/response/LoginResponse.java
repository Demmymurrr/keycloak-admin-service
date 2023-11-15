package ru.demmy.keycloak.protocol.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("access_token")
    private String accessToken;
}
