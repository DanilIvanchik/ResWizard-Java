package com.reswizard.Controllers;

import com.reswizard.DTO.PersonDTO;
import com.reswizard.Models.Person;
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
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(PersonValidator personValidator, RegistrationService registrationService, ModelMapper modelMapper, AuthenticationManager authenticationManager) {
        this.personValidator = personValidator;
        this.registrationService = registrationService;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public String loginPage(){
        return "LoginPage";
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
            return "redirect:/auth/registration";
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
