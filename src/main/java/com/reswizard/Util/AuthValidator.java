package com.reswizard.Util;

import com.reswizard.DTO.AuthenticationDTO;
import com.reswizard.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AuthValidator implements Validator {

    public final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthValidator(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Check if the Validator supports the provided class.
     *
     * @param clazz The class to be checked.
     * @return True if the Validator supports the class, false otherwise.
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return AuthenticationDTO.class.equals(clazz);
    }

    /**
     * Validate the target object and populate errors if validation fails.
     *
     * @param target The object to be validated.
     * @param errors The Errors object to hold validation errors.
     */
    @Override
    public void validate(Object target, Errors errors) {
        AuthenticationDTO authenticationDTO = (AuthenticationDTO) target;
        if (!userService.isUsernamePresent(authenticationDTO.getUsername())){
            errors.rejectValue("username", "", "Oops, it seems there's a typo in your username. Please double-check it.");
        }else if (!userService.isPasswordValid(passwordEncoder.encode(authenticationDTO.getPassword()))){
            errors.rejectValue("password", "", "Oops! That's not the right password. Please check and try again.");
        }
    }
}
