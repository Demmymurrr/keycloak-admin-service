package ru.demmy.keycloak.protocol.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.demmy.keycloak.enums.CommonConst;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Email(message = "Email is invalid", regexp = CommonConst.EMAIL_REGEXP)
    private String email;

    @JsonProperty("password_hash")
    private String passwordHash;

    @JsonProperty("is_device_login")
    private boolean isDeviceLogin = false;

    @NotNull
    @JsonProperty("device")
    private String deviceId;
}
