package com.reswizard.Controllers;

import com.reswizard.Services.AdminService;
import com.reswizard.Services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final AdminService adminService;
    private final PeopleService peopleService;

    @Autowired
    public MainController(AdminService adminService, PeopleService peopleService) {
        this.adminService = adminService;
        this.peopleService = peopleService;
    }

    @GetMapping("/hello")
    public String sayHello(){
        return "MainPageLogin";
    }

    @GetMapping("/helloUser")
    public String helloUser(Model model){
        model.addAttribute("person", peopleService.getCurrentPerson());
        return "MainPage";
    }

    @GetMapping("/admin")
    public String adminPage(){
        adminService.doAdmin();
        return "Admin";
    }
}
