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

    /**
     * Check if this validator supports the given class for validation.
     *
     * @param clazz The class to check.
     * @return True if the class is supported, false otherwise.
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    /**
     * Validate the provided user object and add errors to the Errors object if validation fails.
     *
     * @param target The user object to validate.
     * @param errors The Errors object to which validation errors will be added.
     */
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
