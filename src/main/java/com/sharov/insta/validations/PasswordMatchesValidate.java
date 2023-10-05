package com.sharov.insta.validations;

import com.sharov.insta.annotations.PasswordMatches;
import com.sharov.insta.dto.UserCreateEditDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidate implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        UserCreateEditDto signUpRequest = (UserCreateEditDto) object;
        return signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword());
    }
}
