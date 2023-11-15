package ru.demmy.keycloak.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.demmy.keycloak.validation.validator.LoginRequestConstraintValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LoginRequestConstraintValidator.class)
public @interface LoginRequestValidator {

    String message() default "Login request is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
