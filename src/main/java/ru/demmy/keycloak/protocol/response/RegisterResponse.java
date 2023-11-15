package ru.demmy.keycloak.protocol.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {

    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("confirmation_sent")
    private Boolean confirmationSent;
}
