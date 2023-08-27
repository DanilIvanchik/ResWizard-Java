package com.reswizard.Controllers;

import com.reswizard.DTO.AuthenticationDTO;
import com.reswizard.DTO.PersonDTO;
import com.reswizard.Models.Person;
import com.reswizard.PersonValidator;
import com.reswizard.Services.RegistrationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

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



    @PostMapping("/login")
    public String performLogin(@RequestBody AuthenticationDTO authenticationDTO){
        return null;
    }

    @GetMapping("/login")
    public String loginPage(){
        return "loginPage";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("person") Person person){
        return "Registration";
    }

    @PostMapping("/registration")
    public String performRegistration(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult){

        Person person = convertToPerson(personDTO);

        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()){
            return "redirect:/auth/registration";
        }
        registrationService.register(person);
        return "redirect:/hello";
    }

    private Person convertToPerson(PersonDTO personDTO){
        Person person = modelMapper.map(personDTO, Person.class);
        System.out.println(person.getRole());
        return person;
    }
}
