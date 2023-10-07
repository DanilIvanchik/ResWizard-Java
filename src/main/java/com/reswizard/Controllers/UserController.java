package com.reswizard.Controllers;

import com.reswizard.DTO.UserDTO;
import com.reswizard.Models.User;
import com.reswizard.Services.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private static final Logger logger = Logger.getGlobal();
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/recover_password")
    public String recoverPassword(@ModelAttribute("userDTO") UserDTO userDTO){
        return "RecoveringCredentialsPage";
    }

    @PostMapping("/recover")
    public String recoverPassword(@ModelAttribute("userDTO") @Valid UserDTO userDTO,
                                  BindingResult bindingResult){
        if(!userService.isUserPresentByEmail(userDTO.getEmail())){
            bindingResult.rejectValue("email", "", "Please check the entered information. It appears that the email address is not formatted correctly.");
            return "RecoveringCredentialsPage";
        }
        userService.sendRecoveringEmail(userDTO.getEmail());
        return "RecoverPasswordPage";
    }

    @GetMapping("/reset/{id}")
    public String resetPasswordPage(@PathVariable Integer id, @ModelAttribute("userDTO") UserDTO userDTO, Model model){
        User user = userService.findUserById(id);
        if (!user.getIsInRecovering()){
            return "AccessDeniedPage";
        }
        user.setPassword("");
        model.addAttribute("user", user);
        return "RecoveringPasswordPage";
    }

    @PostMapping("/reset/{id}")
    public String resetPassword(@PathVariable Integer id, @ModelAttribute("user") User user, Model model){
        User currentUser = userService.findUserById(id);
        if (!currentUser.getIsInRecovering()){
            return "AccessDeniedPage";
        }
        userService.resetPassword(currentUser, user.getPassword());
        return "SuccessfulPasswordRecoverPage";
    }

}
