package com.reswizard.Controllers;

import com.reswizard.Models.Person;
import com.reswizard.Models.Resume;
import com.reswizard.Services.PeopleService;
import com.reswizard.Services.ResumeService;
import com.reswizard.Util.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequestMapping("/person")
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
    public String showAllPersonResumes(Model model) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person currentPerson = peopleService.findUserByUsername(authentication.getName());
        model.addAttribute("person", currentPerson);
        model.addAttribute("resumes", currentPerson.getResumes());
        return "ResumePage";
    }

//    @GetMapping("/files/{filename:.+}")
//    @ResponseBody
//    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
//
//        Resource file = storageService.loadAsResource(filename);
//        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
//    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file != null){
            File uploadDir = new File(uploadPath);


            if (!uploadDir.exists() && !file.getOriginalFilename().isEmpty()){
                uploadDir.mkdir();
            }

            String UIDFile = UUID.randomUUID().toString();
            String resultFileName = UIDFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFileName));

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Resume resume = new Resume(resultFileName, uploadPath, peopleService.findUserByUsername(authentication.getName()));
            resumeService.save(resume);
            Person person = peopleService.findUserByUsername(authentication.getName());
            person.getResumes().add(resume);
            peopleService.save(person);
        }

        return "redirect:/person/";
    }

//    @ExceptionHandler(StorageFileNotFoundException.class)
//    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
//        return ResponseEntity.notFound().build();
//    }


}
