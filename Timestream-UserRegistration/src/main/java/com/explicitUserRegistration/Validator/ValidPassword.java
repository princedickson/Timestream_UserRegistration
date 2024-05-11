package com.explicitUserRegistration.Validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface  ValidPassword {

    String message() default "Invalid Password";
    Class<?>[]  groups() default {};
    Class<? extends Payload>[] payload() default {};
}
