package com.reswizard.Controllers;

import com.reswizard.Models.Person;
import com.reswizard.Services.PeopleService;
import com.reswizard.Services.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/person")
public class ResumeController {
    private final PeopleService peopleService;
    private final ResumeService resumeService;

    @Autowired
    public ResumeController(PeopleService peopleService, ResumeService resumeService) {
        this.peopleService = peopleService;
        this.resumeService = resumeService;
    }

    @GetMapping("/current")
    public String showAllPersonResumes(Model model, Principal principal){
        Person currentPerson = peopleService.findUserByUsername(principal.getName());
        model.addAttribute("person", currentPerson);
        return "ResumePage";
    }

    @PostMapping("/upload")
    public String uploadResume(Model model, Principal principal){
        Person currentPerson = peopleService.findUserByUsername(principal.getName());
        model.addAttribute("person", currentPerson);
        return "redirect:/person/current";
    }




}
