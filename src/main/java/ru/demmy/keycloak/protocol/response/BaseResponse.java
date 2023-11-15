package ru.demmy.keycloak.protocol.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.demmy.keycloak.enums.ResponseStatus;

/**
 * BaseResponse is the wrapper for any response that expands specific error codes.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T>{

    private ResponseStatus status;

    @JsonProperty(value = "status_code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String statusCode;

    @JsonProperty(value = "err_msg")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errMsg;

    @JsonProperty(value = "session_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sessionId;

    @JsonProperty(value = "data")
    private T data;

}
