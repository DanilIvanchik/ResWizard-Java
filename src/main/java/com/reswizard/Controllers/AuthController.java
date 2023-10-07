package com.reswizard.Controllers;

import com.reswizard.DTO.AuthenticationDTO;
import com.reswizard.DTO.UserDTO;
import com.reswizard.Models.User;
import com.reswizard.Services.UserService;
import com.reswizard.Util.AuthValidator;
import com.reswizard.Util.UserValidator;
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

    private final UserValidator userValidator;
    private final RegistrationService registrationService;
    private final ModelMapper modelMapper;
    private final AuthValidator authValidator;
    private static final Logger logger = Logger.getGlobal();
    private final UserService userService;

    @Autowired
    public AuthController(
            UserValidator userValidator,
            RegistrationService registrationService,
            ModelMapper modelMapper,
            AuthValidator authValidator,
            UserService userService) {
        this.userValidator = userValidator;
        this.registrationService = registrationService;
        this.modelMapper = modelMapper;
        this.authValidator = authValidator;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@ModelAttribute("authenticationDTO") AuthenticationDTO authenticationDTO) {
        logger.log(Level.INFO, "Displaying login page.");
        return "LoginPage";
    }

    @PostMapping("/login")
    public String loginPage(@ModelAttribute("authenticationDTO") @Valid AuthenticationDTO authenticationDTO,
            BindingResult bindingResult){
        logger.log(Level.INFO, "Processing user login.");

        authValidator.validate(authenticationDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            logger.log(Level.WARNING, "Login validation failed.");
            return "LoginPage";
        }

        return "redirect:/process_login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("userDTO") UserDTO userDTO) {
        logger.log(Level.INFO, "Displaying registration page.");
        return "RegistrationPage";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        logger.log(Level.INFO, "Displaying access denied page.");
        return "AccessDeniedEmailPage";
    }

    @PostMapping("/registration")
    public String performRegistration(
            @ModelAttribute("userDTO") @Valid UserDTO userDTO,
            BindingResult bindingResult){
        logger.log(Level.INFO, "Processing user registration.");

        User user = DTOToUser(userDTO);

        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            logger.log(Level.WARNING, "Registration validation failed.");
            return "RegistrationPage";
        }

        registrationService.register(user);
        logger.log(Level.INFO, "User registration successful. Redirecting to login page.");
        return "redirect:/auth/login";
    }

    private User DTOToUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        logger.log(Level.INFO, "Converting UserDTO to User object: " + user.getUsername());
        return user;
    }

    @GetMapping("/activate/{code}")
    public String activateUser(@ModelAttribute("authenticationDTO") AuthenticationDTO authenticationDTO, Model model, @PathVariable String code ){
        if (userService.isActiveUser(code)){
            model.addAttribute("message", "User account successfully activated!");
        }else{
            model.addAttribute("message", "Activation code is not found.");
        }
        return "LoginPage";
    }
}


