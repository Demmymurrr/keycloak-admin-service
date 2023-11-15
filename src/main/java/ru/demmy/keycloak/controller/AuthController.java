package ru.demmy.keycloak.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.demmy.keycloak.enums.ResponseStatus;
import ru.demmy.keycloak.protocol.ResponseUtil;
import ru.demmy.keycloak.protocol.request.ChangePasswordRequest;
import ru.demmy.keycloak.protocol.request.LoginRequest;
import ru.demmy.keycloak.protocol.request.RegisterRequest;
import ru.demmy.keycloak.service.LoginService;
import ru.demmy.keycloak.service.RegisterService;
import ru.demmy.keycloak.protocol.response.BaseResponse;
import ru.demmy.keycloak.service.UserService;
import ru.demmy.keycloak.validation.constraint.LoginRequestValidator;

@Log4j2
@Validated
@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RegisterService registerService;
    private final LoginService loginService;

    @PostMapping(value = "/register")
    public ResponseEntity<BaseResponse<?>> register(@Valid @RequestBody RegisterRequest registerRequest) throws Exception {
        return ResponseEntity.ok(ResponseUtil.ok(registerService.register(registerRequest)));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<BaseResponse<?>> login(@RequestBody @LoginRequestValidator LoginRequest loginRequest) throws Exception {
        return ResponseEntity.ok(ResponseUtil.ok(loginService.login(loginRequest)));
    }

    @PostMapping(value = "/password/change")
    public ResponseEntity<BaseResponse<?>> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) throws Exception {
        userService.handleChangePasswordRequest(changePasswordRequest);
        return ResponseEntity.ok(BaseResponse.builder().status(ResponseStatus.SUCCESS).build());
    }

}
