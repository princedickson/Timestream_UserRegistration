package com.explicitUserRegistration.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final int MAX_LENGTH = 30;
    private static final int MIN_LENGTH = 5;
    private static final String SPECIAL_CHARACTER = "!@#$%^&*()_+.";
    private static final int REQUIRE_UPPERCASE_COUNT = 1;
    private static int UPPERCASE_COUNT = 0;
    boolean CONTAIN_SPECIAL_CHARACTER = false;


    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final String password, final  ConstraintValidatorContext constraintValidatorContext) {

        if (password == null || password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            return false;
        }
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                UPPERCASE_COUNT = UPPERCASE_COUNT + 1;
            }
            if (SPECIAL_CHARACTER.indexOf(c) != -1) {
                CONTAIN_SPECIAL_CHARACTER = true;
            }
        }

        return UPPERCASE_COUNT >= REQUIRE_UPPERCASE_COUNT && CONTAIN_SPECIAL_CHARACTER;
    }

    public boolean isValid(String password) {
        return isValid(password, null);
    }
}
