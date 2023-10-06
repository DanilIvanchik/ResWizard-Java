package com.reswizard.Controllers;

import com.reswizard.Models.Person;
import com.reswizard.Services.PeopleService;
import com.reswizard.Services.ResumeService;
import com.reswizard.Util.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/resumes")
public class ResumeController {

    private final PeopleService peopleService;
    private final ResumeService resumeService;

    @Value("${upload.file.path}")
    private String uploadPath;

    @Value("${upload.avatar.path}")
    private String avatarUploadPath;

    private static final Logger logger = Logger.getGlobal();

    @Autowired
    public ResumeController(PeopleService peopleService, ResumeService resumeService) {
        this.peopleService = peopleService;
        this.resumeService = resumeService;
    }

    @GetMapping("/")
    public String showAllPersonResumes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logger.log(Level.INFO, "Displaying all resumes for user: " + authentication.getName());

        List<Languages> languageOptions = Arrays.asList(Languages.UKRAINIAN, Languages.CHINESE, Languages.SPANISH, Languages.ENGLISH, Languages.GERMAN);
        model.addAttribute("options", languageOptions);

        Person currentPerson = peopleService.findUserByUsername(authentication.getName());
        model.addAttribute("person", currentPerson);

        if (!resumeService.isAllResumesExist(currentPerson.getResumes(), uploadPath)) {
            model.addAttribute("resumes", resumeService.getPersonsExistedResumes(currentPerson.getResumes(), uploadPath, currentPerson));
            return "SomeOfResumesNotFoundPage";
        }

        if (currentPerson.getResumes() != null) {
            model.addAttribute("resumes", currentPerson.getResumes());
        }

        return "ResumePage";
    }

    @PostMapping("/")
    public String handleResumeUpload(@ModelAttribute("option") Languages selectedLanguage,
                                     @RequestParam("file") MultipartFile file) throws IOException {
        if (resumeService.getFileSizeMegabytes(file) > 1){
            throw new MaxUploadSizeExceededException(file.getSize());
        }
        logger.log(Level.INFO, "Uploading resume file: " + file.getOriginalFilename());
        resumeService.handleResumeFileUpload(file, uploadPath, selectedLanguage);

        return "redirect:/resumes/";
    }

    @RequestMapping(value = "/{id}")
    public String handleResumeDownload(@PathVariable("id") int ResumeId,
                                                   HttpServletResponse response) {
        String fileName = resumeService.findResumeById(ResumeId).getTitle();
        if (!resumeService.isResumeExist(fileName, uploadPath)){
            logger.log(Level.INFO, "File not found: " + fileName);

            return "redirect:/resumes/";
        }

        logger.log(Level.INFO, "Downloading resume file: " + fileName);
        resumeService.handleResumeFileDownload(fileName, response, uploadPath);
        return "redirect:/resumes/";
    }

    @GetMapping(value = "/{key}")
    public String showResumePage(@PathVariable("key") String key,
                                 Model model) {
        Optional<Person> currentPerson = peopleService.findByResumePassKey(key);
        if (currentPerson.isEmpty()){
            return "AccessDeniedPage";
        }
        Person person = currentPerson.get();
        System.out.println(person.getResumePassKey());
        if (person.getActivationCode()!=null){
            logger.log(Level.WARNING, "Users " + person.getUsername()  + " account is not activated. Access denied.");
            return "AccessDeniedEmailPage";
        }
        model.addAttribute("person", person);

        model.addAttribute("resumes", resumeService.getPersonsExistedResumes(person.getResumes(), uploadPath, person));

        logger.log(Level.INFO, "Displaying resumes for user: " + person.getUsername());

        return "ResumeResultPage";
    }

    @PostMapping("/add_message")
    public String addPersonResumeMessage(@ModelAttribute("message") String message) {
        peopleService.isMessageLengthValid(message);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Person currentPerson = peopleService.findUserByUsername(authentication.getName());

        currentPerson.setMessage(message);
        peopleService.save(currentPerson);

        logger.log(Level.INFO, "Added a message to the resume for user: " + currentPerson.getUsername());

        return "redirect:/resumes/";
    }

    @PostMapping("/upload_avatar")
    public String handleAvatarUpload(@RequestParam("file") MultipartFile file) throws IOException {
        logger.log(Level.INFO, "Uploading avatar image: " + file.getOriginalFilename());
        peopleService.handleAvatarFileUpload(file, avatarUploadPath);

        return "redirect:/resumes/";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteResume(@PathVariable("id") int resumeId) {
        resumeService.deletePersonResumeFromEditPage(resumeId, uploadPath);

        logger.log(Level.INFO, "Deleted resume with ID: " + resumeId);

        return "redirect:/resumes/";
    }

    @ExceptionHandler
    private String handleIncorrectAvatarFormatException(IncorrectAvatarFormatException e) {
        logger.log(Level.SEVERE, "Incorrect Avatar Format: " + e.getMessage());
//        IncorrectAvatarFormatExceptionResponse response = new IncorrectAvatarFormatExceptionResponse();
//        response.setMessage(e.getMessage());
//        response.setTime(System.currentTimeMillis());
//        new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        return "IncorrectFormatExceptionPage";
    }

    @ExceptionHandler
    private String handleIncorrectResumeFormatException(IncorrectResumeFormatException e) {
        logger.log(Level.SEVERE, "Incorrect Resume Format: " + e.getMessage());
//        IncorrectFormatExceptionResponse response = new IncorrectFormatExceptionResponse();
//        response.setMessage(e.getMessage());
//        response.setTime(System.currentTimeMillis());
//        new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        return "IncorrectFormatExceptionPage";
    }

    @ExceptionHandler
    private String handleMessageLengthException(MessageLengthException e) {
        logger.log(Level.SEVERE, "Message Length Exception: " + e.getMessage());
//        MessageLengthExceptionResponse response = new MessageLengthExceptionResponse();
//        response.setMessage(e.getMessage());
//        response.setTime(System.currentTimeMillis());
//        new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        return "MessageLengthExceptionPage";
    }

}

