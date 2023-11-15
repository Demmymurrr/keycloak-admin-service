package ru.demmy.keycloak.controller.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.demmy.keycloak.enums.ErrorCode;
import ru.demmy.keycloak.exception.AppException;
import ru.demmy.keycloak.protocol.ResponseUtil;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ErrorController {

    @ExceptionHandler(value = {BindException.class})
    public ResponseEntity<?> validationExceptionHandler(HttpServletRequest request, Exception e) {
        return ResponseEntity
                .badRequest()
                .body(ResponseUtil.error(String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage()));
    }

    @ExceptionHandler(value = {AppException.class})
    public ResponseEntity<?> appExceptionHandler(HttpServletRequest request, AppException e) {
        return ResponseEntity
                .badRequest()
                .body(ResponseUtil.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(value = WebApplicationException.class)
    public ResponseEntity<?> webApplicationExceptionHandler(HttpServletRequest request, WebApplicationException e) {
        HttpStatus httpStatus = HttpStatus.valueOf(e.getResponse().getStatus());
        return ResponseEntity
                .badRequest()
                .body(ResponseUtil.error(String.valueOf(httpStatus.value()), e.getMessage()));
    }

    @ExceptionHandler(value = ProcessingException.class)
    public ResponseEntity<?> processingExceptionHandler(HttpServletRequest request, ProcessingException e) {
        ErrorCode errorCode = ErrorCode.KEYCLOAK_UNAVAILABLE;
        return ResponseEntity
                .internalServerError()
                .body(ResponseUtil.error(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex, WebRequest request) {
        return ResponseEntity
                .internalServerError()
                .body(ResponseUtil.error(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), ex.getMessage()));
    }
}
