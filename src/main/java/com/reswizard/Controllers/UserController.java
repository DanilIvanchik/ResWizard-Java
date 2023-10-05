package com.reswizard.Controllers;

import com.reswizard.DTO.PersonDTO;
import com.reswizard.Models.Person;
import com.reswizard.Services.PeopleService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/user")
public class UserController {
    private final PeopleService peopleService;
    private static final Logger logger = Logger.getGlobal();
    private final ModelMapper modelMapper;

    public UserController(PeopleService peopleService, ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/recover_password")
    public String recoverPassword(@ModelAttribute("personDTO") PersonDTO personDTO){
        return "RecoveringCredentialsPage";
    }

    @PostMapping("/recover")
    public String recoverPassword(@ModelAttribute("personDTO") @Valid PersonDTO personDTO,
                                  BindingResult bindingResult){
        if(!peopleService.isUserPresentByEmail(personDTO.getEmail())){
            bindingResult.rejectValue("email", "", "Please check the entered information. It appears that the email address is not formatted correctly.");
            return "RecoveringCredentialsPage";
        }
        peopleService.sendRecoveringEmail(personDTO.getEmail());
        return "RecoverPasswordPage";
    }

    @GetMapping("/reset/{id}")
    public String resetPasswordPage(@PathVariable Integer id, @ModelAttribute("personDTO") PersonDTO personDTO, Model model){
        Person person = peopleService.findPersonById(id);
        if (!person.getIsInRecovering()){
            return "AccessDeniedPage";
        }
        person.setPassword("");
        model.addAttribute("person", person);
        return "RecoveringPasswordPage";
    }

    @PostMapping("/reset/{id}")
    public String resetPassword(@PathVariable Integer id, @ModelAttribute("person") Person person, Model model){
        Person currentUser = peopleService.findPersonById(id);
        if (!currentUser.getIsInRecovering()){
            return "AccessDeniedPage";
        }
        peopleService.resetPassword(currentUser, person.getPassword());
        return "SuccessfulPasswordRecoverPage";
    }

//    private PersonDTO personToDTO(Person person) {
//        PersonDTO personDTO = modelMapper.map(person, PersonDTO.class);
//        return personDTO;
//    }
}
