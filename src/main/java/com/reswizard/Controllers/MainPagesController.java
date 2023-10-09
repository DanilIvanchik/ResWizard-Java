package com.reswizard.Controllers;

import com.reswizard.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPagesController {

    private final UserService userService;

    @Autowired
    public MainPagesController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the main page.
     *
     * @return The main page view.
     */
    @GetMapping("/hello")
    public String sayHello() {
        return "MainPage";
    }

    /**
     * Displays a user-specific main page.
     *
     * @param model The model to add attributes.
     * @return The user-specific main page view.
     */
    @GetMapping("/helloUser")
    public String helloUser(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "MainUserPage";
    }
}

