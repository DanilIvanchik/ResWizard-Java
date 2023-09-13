package com.reswizard.Controllers;

import com.reswizard.DTO.AuthenticationDTO;
import com.reswizard.DTO.PersonDTO;
import com.reswizard.Models.Person;
import com.reswizard.Util.AuthValidator;
import com.reswizard.Util.PersonValidator;
import com.reswizard.Services.RegistrationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final PersonValidator personValidator;
    private final RegistrationService registrationService;
    private final ModelMapper modelMapper;
    private final AuthValidator authValidator;

    @Autowired
    public AuthController(PersonValidator personValidator, RegistrationService registrationService, ModelMapper modelMapper, AuthValidator authValidator) {
        this.personValidator = personValidator;
        this.registrationService = registrationService;
        this.modelMapper = modelMapper;
        this.authValidator = authValidator;
    }

    @GetMapping("/login")
    public String loginPage(@ModelAttribute("authenticationDTO") AuthenticationDTO authenticationDTO){
        return "LoginPage";
    }

    @PostMapping("/login")
    public String loginPage(@ModelAttribute("authenticationDTO") @Valid AuthenticationDTO authenticationDTO, BindingResult bindingResult){
        authValidator.validate(authenticationDTO, bindingResult);
        if (bindingResult.hasErrors()){
            return "LoginPage";
        }
        return "redirect:/process_login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("personDTO") PersonDTO personDTO){

        return "RegistrationPage";
    }

    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("personDTO") @Valid PersonDTO personDTO, BindingResult bindingResult){

        Person person = convertToPerson(personDTO);

        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()){
            return "RegistrationPage";
        }
        registrationService.register(person);
        return "redirect:/auth/login";
    }

    private Person convertToPerson(PersonDTO personDTO){
        Person person = modelMapper.map(personDTO, Person.class);
        System.out.println(person.getRole());
        return person;
    }
}
