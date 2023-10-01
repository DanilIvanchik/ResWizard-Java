package com.reswizard.Util;

import com.reswizard.DTO.AuthenticationDTO;
import com.reswizard.Services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AuthValidator implements Validator {

    public final PeopleService peopleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthValidator(PeopleService peopleService, PasswordEncoder passwordEncoder) {
        this.peopleService = peopleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return AuthenticationDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AuthenticationDTO authenticationDTO = (AuthenticationDTO) target;
        if (!peopleService.isUsernamePresent(authenticationDTO.getUsername())){
            errors.rejectValue("username", "", "Oops, it seems there's a typo in your username. Please double-check it.");
        }else if (!peopleService.isPasswordValid(passwordEncoder.encode(authenticationDTO.getPassword()))){
            errors.rejectValue("password", "", "Oops! That's not the right password. Please check and try again.");
        }
    }
}
