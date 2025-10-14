package com.zixi.usermanagementsystem.validator;

import com.zixi.usermanagementsystem.model.request.UserRegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordValid, UserRegisterRequest> {

    @Override
    public boolean isValid(UserRegisterRequest user, ConstraintValidatorContext context) {
        return user.getPassword().equals(user.getCheckPassword());
    }
}
