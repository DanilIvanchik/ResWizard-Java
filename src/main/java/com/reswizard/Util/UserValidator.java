package com.reswizard.Util;

import com.reswizard.Models.User;
import com.reswizard.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    public final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        if (userService.isUsernamePresent(user.getUsername())){
            errors.rejectValue("username", "", "The username you've chosen is unavailable. Please select a different one.");
        }
        if (userService.isUserPresentByEmail(user.getEmail())){
            errors.rejectValue("email", "", "This email address is already registered. Please use a different email address.");
        }

    }
}
