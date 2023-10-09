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

    /**
     * Displays the password recovery page.
     *
     * @param userDTO The UserDTO object containing user information.
     * @return The name of the password recovery page view.
     */
    @GetMapping("/recover_password")
    public String recoverPassword(@ModelAttribute("userDTO") UserDTO userDTO){
        return "RecoveringCredentialsPage";
    }

    /**
     * Handles the password recovery process.
     *
     * @param userDTO        The UserDTO object containing user information.
     * @param bindingResult  The result of data binding and validation.
     * @return The name of the password recovery confirmation page view or the recovery page if there are errors.
     */
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

    /**
     * Displays the password reset page.
     *
     * @param id    The ID of the user for password reset.
     * @param userDTO   The UserDTO object containing user information.
     * @param model The model to add attributes.
     * @return The name of the password reset page view or an access denied page if the user is not in the recovery process.
     */
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

    /**
     * Handles the password reset process.
     *
     * @param id    The ID of the user for password reset.
     * @param user  The User object with the new password.
     * @param model The model to add attributes.
     * @return The name of the successful password recovery page view or an access denied page if the user is not in the recovery process.
     */
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

