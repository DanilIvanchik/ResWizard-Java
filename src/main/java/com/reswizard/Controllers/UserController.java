package com.reswizard.Controllers;

import com.reswizard.DTO.PersonDTO;
import com.reswizard.Services.PeopleService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@Controller
@RequestMapping("/user")
public class UserController {
    private final PeopleService peopleService;
    private static final Logger logger = Logger.getGlobal();

    public UserController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping("/recover_password")
    public String recoverPassword(@ModelAttribute("personDTO") PersonDTO personDTO){
        return "RecoveringCredentialsPage";
    }

    @PostMapping("/recover")
    public String recoverPassword(@ModelAttribute("personDTO") @Valid PersonDTO personDTO,
                                  BindingResult bindingResult){
        System.out.println(personDTO.getEmail());
        return "RecoverPasswordPage";
    }
}
