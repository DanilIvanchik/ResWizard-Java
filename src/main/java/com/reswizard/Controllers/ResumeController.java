package com.reswizard.Controllers;

import com.reswizard.Models.User;
import com.reswizard.Services.UserService;
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

    private final UserService userService;
    private final ResumeService resumeService;

    @Value("${upload.file.path}")
    private String uploadPath;

    @Value("${upload.avatar.path}")
    private String avatarUploadPath;

    private static final Logger logger = Logger.getGlobal();

    @Autowired
    public ResumeController(UserService userService, ResumeService resumeService) {
        this.userService = userService;
        this.resumeService = resumeService;
    }

    @GetMapping("/")
    public String showAllUsersResumes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logger.log(Level.INFO, "Displaying all resumes for user: " + authentication.getName());

        List<Languages> languageOptions = Arrays.asList(Languages.UKRAINIAN, Languages.CHINESE, Languages.SPANISH, Languages.ENGLISH, Languages.GERMAN);
        model.addAttribute("options", languageOptions);

        User currentUser = userService.findUserByUsername(authentication.getName());
        model.addAttribute("user", currentUser);

        if (!resumeService.isAllResumesExist(currentUser.getResumes(), uploadPath)) {
            model.addAttribute("resumes", resumeService.getUsersExistingResumes(currentUser.getResumes(), uploadPath, currentUser));
            return "SomeOfResumesNotFoundPage";
        }

        if (currentUser.getResumes() != null) {
            model.addAttribute("resumes", currentUser.getResumes());
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
        Optional<User> currentUser = userService.findByResumePassKey(key);
        if (currentUser.isEmpty()){
            return "AccessDeniedPage";
        }
        User user = currentUser.get();
        if (user.getActivationCode()!=null){
            logger.log(Level.WARNING, "Users " + user.getUsername()  + " account is not activated. Access denied.");
            return "AccessDeniedEmailPage";
        }
        model.addAttribute("user", user);

        model.addAttribute("resumes", resumeService.getUsersExistingResumes(user.getResumes(), uploadPath, user));

        logger.log(Level.INFO, "Displaying resumes for user: " + user.getUsername());

        return "ResumeResultPage";
    }

    @PostMapping("/add_message")
    public String addUserResumeMessage(@ModelAttribute("message") String message) {
        userService.isMessageLengthValid(message);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userService.findUserByUsername(authentication.getName());

        currentUser.setMessage(message);
        userService.save(currentUser);

        logger.log(Level.INFO, "Added a message to the resume for user: " + currentUser.getUsername());

        return "redirect:/resumes/";
    }

    @PostMapping("/upload_avatar")
    public String handleAvatarUpload(@RequestParam("file") MultipartFile file) throws IOException {
        logger.log(Level.INFO, "Uploading avatar image: " + file.getOriginalFilename());
        userService.handleAvatarFileUpload(file, avatarUploadPath);

        return "redirect:/resumes/";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteResume(@PathVariable("id") int resumeId) {
        resumeService.deleteUsersResumeFromEditPage(resumeId, uploadPath);

        logger.log(Level.INFO, "Deleted resume with ID: " + resumeId);

        return "redirect:/resumes/";
    }

    @ExceptionHandler
    private String handleIncorrectAvatarFormatException(IncorrectAvatarFormatException e) {
        logger.log(Level.SEVERE, "Incorrect Avatar Format: " + e.getMessage());
        return "IncorrectFormatExceptionPage";
    }

    @ExceptionHandler
    private String handleIncorrectResumeFormatException(IncorrectResumeFormatException e) {
        logger.log(Level.SEVERE, "Incorrect Resume Format: " + e.getMessage());
        return "IncorrectFormatExceptionPage";
    }

    @ExceptionHandler
    private String handleMessageLengthException(MessageLengthException e) {
        logger.log(Level.SEVERE, "Message Length Exception: " + e.getMessage());
        return "MessageLengthExceptionPage";
    }

}


