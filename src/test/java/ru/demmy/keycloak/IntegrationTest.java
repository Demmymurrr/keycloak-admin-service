package ru.demmy.keycloak;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.MountableFile;
import ru.demmy.keycloak.client.KeycloakClient;
import ru.demmy.keycloak.enums.ErrorCode;
import ru.demmy.keycloak.mock.AuthMock;
import ru.demmy.keycloak.protocol.request.ChangePasswordRequest;
import ru.demmy.keycloak.protocol.request.LoginRequest;
import ru.demmy.keycloak.protocol.request.RegisterRequest;
import ru.demmy.keycloak.protocol.response.BaseResponse;

import java.util.Optional;

@Log4j2
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = BackendApplication.class)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {IntegrationTest.Initializer.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationTest {

    @Autowired
    private AuthMock authMock;

    @Autowired
    private KeycloakClient keycloakClient;

    @Container
    public static GenericContainer<?> keycloak =
            new GenericContainer<>("bitnami/keycloak:22.0.1")
                    .withCopyFileToContainer(
                            MountableFile.forClasspathResource("keycloak/realm-export.json"),
                            "/opt/bitnami/keycloak/data/import/realm-export.json"
                    )
                    .withEnv("KEYCLOAK_EXTRA_ARGS", "--import-realm")
                    .withEnv("KEYCLOAK_DATABASE_VENDOR", "dev-mem")
                    .withExposedPorts(8080);

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            keycloak.start();
            String keycloakUrl = "http://" + keycloak.getHost() + ":" + keycloak.getFirstMappedPort();
            TestPropertyValues.of(
                    // Keycloak
                    "keycloak.url=" + keycloakUrl
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Test
    void register_success() throws Exception {
        String email = "register_test@kpbs.ru";
        RegisterRequest registerRequest = RegisterRequest.builder()
                .deviceId("test_device_id")
                .email(email)
                .passwordHash("1234")
                .build();

        MvcResult mvcResult = authMock.performRegister(registerRequest);
        Assertions.assertEquals(200, mvcResult.getResponse().getStatus());

        Optional<UserRepresentation> user = keycloakClient.findUser(email);
        Assertions.assertTrue(user.isPresent());
    }

    @Test
    void login_email_success() throws Exception {
        String email = "login_test@kpbs.ru";
        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .passwordHash("1234")
                .deviceId("test_device_id")
                .build();
        MvcResult mvcResult = authMock.performLogin(loginRequest);
        Assertions.assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    void change_device_success() throws Exception {
        String email = "change_device_test@kpbs.ru";
        String testDevice = "test_device_id_2";
        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .passwordHash("1234")
                .deviceId(testDevice)
                .build();
        MvcResult loginResult = authMock.performLogin(loginRequest);
        Assertions.assertEquals(400, loginResult.getResponse().getStatus());
        BaseResponse<Object> errorResponse = authMock.mapResponse(loginResult, Object.class);
        Assertions.assertEquals(ErrorCode.USER_CREDENTIALS_IS_INVALID.getCode(), errorResponse.getStatusCode());

        UserRepresentation userRepresentation = keycloakClient.findUser(email).orElseThrow();
        keycloakClient.changeDeviceId(userRepresentation.getId(), testDevice);

        MvcResult loginResult2 = authMock.performLogin(loginRequest);
        Assertions.assertEquals(200, loginResult2.getResponse().getStatus());
    }

    @Test
    void change_password_success() throws Exception {
        String email = "change_password_test@kpbs.ru";
        String oldPassword = "1234";
        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .passwordHash(oldPassword)
                .deviceId("test_device_id")
                .build();
        MvcResult firstLoginResult = authMock.performLogin(loginRequest);
        Assertions.assertEquals(200, firstLoginResult.getResponse().getStatus());

        String newPassword = "password";
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .email(email)
                .passwordHash(oldPassword)
                .newPasswordHash(newPassword)
                .build();
        MvcResult changePasswordResult = authMock.performChangePassword(changePasswordRequest);
        Assertions.assertEquals(200, changePasswordResult.getResponse().getStatus());

        loginRequest.setPasswordHash(newPassword);
        MvcResult secondLoginResult = authMock.performLogin(loginRequest);
        Assertions.assertEquals(200, secondLoginResult.getResponse().getStatus());
    }


}
