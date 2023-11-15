package ru.demmy.keycloak.protocol.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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
public class ChangePasswordRequest {

    @NotNull
    @NotEmpty
    @Email(message = "Email is invalid", regexp = CommonConst.EMAIL_REGEXP)
    private String email;

    @NotNull
    @NotEmpty
    @JsonProperty("password_hash")
    private String passwordHash;

    @NotNull
    @NotEmpty
    @JsonProperty("new_password_hash")
    private String newPasswordHash;
}
