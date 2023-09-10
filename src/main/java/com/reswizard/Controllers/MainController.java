package com.reswizard.Controllers;

import com.reswizard.Services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final AdminService adminService;

    @Autowired
    public MainController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/hello")
    public String sayHello(){
        return "MainPageLogin";
    }

    @GetMapping("/helloUser")
    public String helloUser(){
        return "MainPage";
    }

    @GetMapping("/admin")
    public String adminPage(){
        adminService.doAdmin();
        return "Admin";
    }
}
