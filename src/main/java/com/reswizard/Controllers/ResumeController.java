package com.reswizard.Controllers;

import com.reswizard.Models.Person;
import com.reswizard.Models.Resume;
import com.reswizard.Services.PeopleService;
import com.reswizard.Services.ResumeService;
import com.reswizard.Util.Languages;
import com.reswizard.Util.StorageFileNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
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
import java.util.Arrays;
import java.util.List;

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

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public ResumeController(PeopleService peopleService, ResumeService resumeService) {
        this.peopleService = peopleService;
        this.resumeService = resumeService;
    }

    @GetMapping("/")
    public String showAllPersonResumes(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Languages> languageOptions = Arrays.asList(Languages.UKRAINIAN, Languages.CHINESE, Languages.SPANISH, Languages.ENGLISH, Languages.GERMAN);
        model.addAttribute("options", languageOptions);
        model.addAttribute("selectedOption", Languages.ENGLISH);
        Person currentPerson = peopleService.findUserByUsername(authentication.getName());
        model.addAttribute("person", currentPerson);
        model.addAttribute("resumes", currentPerson.getResumes());
        return "ResumePage";
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("mySelect") Languages selectedLanguage, @RequestParam("file") MultipartFile file) throws IOException {
        resumeService.handleResumeFileUpload(file, uploadPath, selectedLanguage);
        return "redirect:/resumes/";
    }


    @RequestMapping(value = "/{id}")
    public @ResponseBody void handleFileDownload(@PathVariable("id") int ResumeId, HttpServletResponse response){
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

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }


}
