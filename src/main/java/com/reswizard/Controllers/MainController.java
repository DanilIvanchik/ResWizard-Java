package com.reswizard.Controllers;

import com.reswizard.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final UserService userService;

    @Autowired
    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String sayHello(){
        return "MainPage";
    }

    @GetMapping("/helloUser")
    public String helloUser(Model model){
        model.addAttribute("user", userService.getCurrentUser());
        return "MainUserPage";
    }
}
