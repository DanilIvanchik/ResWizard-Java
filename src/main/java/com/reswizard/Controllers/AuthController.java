package com.reswizard.Controllers;

import com.reswizard.DTO.AuthenticationDTO;
import com.reswizard.DTO.PersonDTO;
import com.reswizard.Models.Person;
import com.reswizard.Services.AuthenticationService;
import com.reswizard.Services.PeopleService;
import com.reswizard.Util.AuthValidator;
import com.reswizard.Util.PersonValidator;
import com.reswizard.Services.RegistrationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final PersonValidator personValidator;
    private final RegistrationService registrationService;
    private final ModelMapper modelMapper;
    private final AuthValidator authValidator;
    private static final Logger logger = Logger.getGlobal();
    private final PeopleService peopleService;
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthController(
            PersonValidator personValidator,
            RegistrationService registrationService,
            ModelMapper modelMapper,
            AuthValidator authValidator,
            PeopleService peopleService, AuthenticationService authenticationService) {
        this.personValidator = personValidator;
        this.registrationService = registrationService;
        this.modelMapper = modelMapper;
        this.authValidator = authValidator;
        this.peopleService = peopleService;
        this.authenticationService = authenticationService;
    }

    // Handles HTTP GET requests to the "/auth/login" path, displaying the login page.
    @GetMapping("/login")
    public String loginPage(@ModelAttribute("authenticationDTO") AuthenticationDTO authenticationDTO) {
        logger.log(Level.INFO, "Displaying login page.");
        return "LoginPage";
    }

    // Handles HTTP POST requests to the "/auth/login" path, processing user login.
    @PostMapping("/login")
    public String loginPage(Model model,
            @ModelAttribute("authenticationDTO") @Valid AuthenticationDTO authenticationDTO,
            BindingResult bindingResult){
        logger.log(Level.INFO, "Processing user login.");

        // Validate the submitted authentication data.
        authValidator.validate(authenticationDTO, bindingResult);

        // If validation errors exist, return to the login page with error messages.
        if (bindingResult.hasErrors()) {
            logger.log(Level.WARNING, "Login validation failed.");
            return "LoginPage";
        }

        // Redirect to the login processing page.
        logger.log(Level.INFO, "Redirecting to login processing page.");
        return "redirect:/process_login";
    }

    // Handles HTTP GET requests to the "/auth/registration" path, displaying the registration page.
    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("personDTO") PersonDTO personDTO) {
        logger.log(Level.INFO, "Displaying registration page.");
        return "RegistrationPage";
    }

    // Handles HTTP GET requests to the "/auth/access-denied" path, displaying the access denied page.
    @GetMapping("/access-denied")
    public String accessDenied() {
        logger.log(Level.INFO, "Displaying access denied page.");
        return "AccessDeniedEmailPage";
    }

    // Handles HTTP POST requests to the "/auth/registration" path, processing user registration.
    @PostMapping("/registration")
    public String performRegistration(
            @ModelAttribute("personDTO") @Valid PersonDTO personDTO,
            BindingResult bindingResult){
        logger.log(Level.INFO, "Processing user registration.");

        // Convert the DTO to a Person object.
        Person person = DTOToPerson(personDTO);

        // Validate the submitted person data.
        personValidator.validate(person, bindingResult);

        // If validation errors exist, return to the registration page with error messages.
        if (bindingResult.hasErrors()) {
            logger.log(Level.WARNING, "Registration validation failed.");
            return "RegistrationPage";
        }

        // Register the user and redirect to the login page.
        registrationService.register(person);
        logger.log(Level.INFO, "User registration successful. Redirecting to login page.");
        return "redirect:/auth/login";
    }

    // Converts a PersonDTO to a Person object using ModelMapper.
    private Person DTOToPerson(PersonDTO personDTO) {
        Person person = modelMapper.map(personDTO, Person.class);
        logger.log(Level.INFO, "Converting PersonDTO to Person object: " + person.getUsername());
        return person;
    }

    @GetMapping("/activate/{code}")
    public String activateUser(@ModelAttribute("authenticationDTO") AuthenticationDTO authenticationDTO, Model model, @PathVariable String code ){
        if (peopleService.isActiveUser(code)){
            model.addAttribute("message", "User account successfully activated!");
        }else{
            model.addAttribute("message", "Activation code is not found.");
        }
        return "LoginPage";
    }
}


