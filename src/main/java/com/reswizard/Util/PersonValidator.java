package com.reswizard.Util;

import com.reswizard.Models.Person;
import com.reswizard.Services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PersonValidator implements Validator {
    public final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        if (peopleService.isUsernamePresent(person.getUsername())){
            errors.rejectValue("username", "", "The username you've chosen is unavailable. Please select a different one.");
        }
        if (peopleService.isUserPresentByEmail(person.getEmail())){
            errors.rejectValue("email", "", "This email address is already registered. Please use a different email address.");
        }

    }
}
