package com.zixi.usermanagementsystem.validator;

import com.zixi.usermanagementsystem.controller.dto.UserRegisterDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordValid, UserRegisterDTO> {

    @Override
    public boolean isValid(UserRegisterDTO user, ConstraintValidatorContext context) {
        return user.getPassword().equals(user.getCheckPassword());
    }
}
