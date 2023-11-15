package ru.demmy.keycloak.protocol;

import ru.demmy.keycloak.enums.ResponseStatus;
import ru.demmy.keycloak.protocol.response.BaseResponse;

public class ResponseUtil {

    public static <T> BaseResponse<T> ok(T payload) {
        return BaseResponse.<T>builder()
                .status(ResponseStatus.SUCCESS)
                .data(payload)
                .build();
    }

    public static BaseResponse<Object> error(String errCode, String errMsg){
        return BaseResponse.builder()
                .status(ResponseStatus.ERROR)
                .errMsg(errMsg)
                .statusCode(errCode)
                .build();
    }
}
