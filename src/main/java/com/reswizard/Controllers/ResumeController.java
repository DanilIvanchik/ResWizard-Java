package com.reswizard.Controllers;

import com.reswizard.Models.Person;
import com.reswizard.Services.PeopleService;
import com.reswizard.Services.ResumeService;
import com.reswizard.Util.Languages;
import com.reswizard.Util.StorageFileNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/resumes")
public class ResumeController {
    private final PeopleService peopleService;
    private final ResumeService resumeService;

    @Value("${upload.file.path}")
    private String uploadPath;

    @Value("${upload.avatar.path}")
    private String avatarUploadPath;

    @Autowired
    public ResumeController(PeopleService peopleService, ResumeService resumeService) {
        this.peopleService = peopleService;
        this.resumeService = resumeService;
    }

    @GetMapping("/")
    public String showAllPersonResumes(Model model, HttpSession session){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Languages> languageOptions = Arrays.asList(Languages.UKRAINIAN, Languages.CHINESE, Languages.SPANISH, Languages.ENGLISH, Languages.GERMAN);
        model.addAttribute("options", languageOptions);
        model.addAttribute("selectedOption", Languages.ENGLISH);
        Person currentPerson = peopleService.findUserByUsername(authentication.getName());
        model.addAttribute("person", currentPerson);
        if (currentPerson.getResumes() != null){
            model.addAttribute("resumes", currentPerson.getResumes());
        }
        return "ResumePage";
    }

    @PostMapping("/")
    public String handleResumeUpload(@ModelAttribute("option") Languages selectedLanguage, @RequestParam("file") MultipartFile file) throws IOException {
        resumeService.handleResumeFileUpload(file, uploadPath, selectedLanguage);
        return "redirect:/resumes/";
    }


    @RequestMapping(value = "/{id}")
    public @ResponseBody void handleResumeDownload(@PathVariable("id") int ResumeId, HttpServletResponse response){
        String fileName = resumeService.findResumeById(ResumeId).getTitle();
        resumeService.handleResumeFileDownload(fileName, response, uploadPath);
    }

    @GetMapping(value = "/show_resumes/{id}")
    public String showResumePage(@PathVariable("id") int PersonId,Model model){
        Person currentPerson = peopleService.findPersonById(PersonId);
        model.addAttribute("person", currentPerson);
        model.addAttribute("resumes", currentPerson.getResumes());
        return "ResumeResult";
    }

    @PostMapping("/add_message")
    public String addPersonResumeMessage(@ModelAttribute("message") String message){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = peopleService.findUserByUsername(authentication.getName());
        person.setMessage(message);
        peopleService.save(person);
        return "redirect:/resumes/";
    }

    @PostMapping("/upload_avatar")
    public String handleAvatarUpload(@RequestParam("file") MultipartFile file,  HttpSession session) throws IOException {
        peopleService.handleAvatarFileUpload(file, avatarUploadPath);
        return "redirect:/resumes/";
    }

//    @ExceptionHandler(StorageFileNotFoundException.class)
//    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
//        return ResponseEntity.notFound().build();
//    }


}
