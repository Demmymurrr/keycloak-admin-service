package ru.demmy.keycloak.mock;

import com.fasterxml.jackson.databind.JavaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.demmy.keycloak.protocol.request.ChangePasswordRequest;
import ru.demmy.keycloak.protocol.request.RegisterRequest;
import ru.demmy.keycloak.service.ObjectMapperService;
import ru.demmy.keycloak.protocol.request.LoginRequest;
import ru.demmy.keycloak.protocol.response.BaseResponse;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class AuthMock {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapperService oms;

    public <T>BaseResponse<T> mapResponse(MvcResult result, Class<T> tClass) throws IOException {
        JavaType javaType = oms.objectMapper().getTypeFactory().constructParametricType(BaseResponse.class, tClass);
        return oms.objectMapper().readValue(result.getResponse().getContentAsByteArray(), javaType);
    }

    public MvcResult performRegister(RegisterRequest registerRequest) throws Exception {
        String json = oms.objectToJsonStringOrThrow(registerRequest);
        return mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();
    }

    public MvcResult performLogin(LoginRequest loginRequest) throws Exception {
        String json = oms.objectToJsonStringOrThrow(loginRequest);
        return mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();
    }

    public MvcResult performChangePassword(ChangePasswordRequest changePasswordRequest) throws Exception {
        String json = oms.objectToJsonStringOrThrow(changePasswordRequest);
        return mvc.perform(post("/auth/password/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();
    }

}
