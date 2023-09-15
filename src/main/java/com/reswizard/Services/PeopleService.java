package com.reswizard.Services;

import com.reswizard.Models.Person;
import com.reswizard.Models.Resume;
import com.reswizard.Repositories.PeopleRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepo peopleRepo;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public PeopleService(PeopleRepo peopleRepo, PasswordEncoder passwordEncoder) {
        this.peopleRepo = peopleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isDuplicate(String name) {
        return peopleRepo.findByUsername(name).isPresent();
    }

    public boolean isUserPresentByEmail(String email) {
        return peopleRepo.findByEmail(email).isPresent();
    }

    public Person findUserByUsername(String name) {
        if (peopleRepo.findByUsername(name).isPresent()) {
            Optional<Person> person = peopleRepo.findByUsername(name);
            return person.get();
        } else {
            return null;
        }
    }

    @Transactional
    public void handleAvatarFileUpload(MultipartFile file, String uploadPath) throws IOException {
        if (file == null || file.isEmpty()) {
            return;
        }

        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());
        String filePath = uploadPath + uniqueFileName;

        file.transferTo(new File(filePath));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Person> person = peopleRepo.findByUsername(username);
        person.get().setAvatarTitle(uniqueFileName);
        peopleRepo.save(person.get());
    }

    private String generateUniqueFileName(String originalFileName) {
        String uidFile = UUID.randomUUID().toString();
        int lastDotIndex = originalFileName.lastIndexOf(".");
        String extension = (lastDotIndex >= 0) ? originalFileName.substring(lastDotIndex) : "";
        return uidFile + extension;
    }

    public boolean isPasswordValid(String password) {
        return peopleRepo.findByPassword(passwordEncoder.encode(password)).isPresent();
    }

    public Person findPersonById(int id) {
        return peopleRepo.findPersonById(id).get();
    }

    @Transactional
    public void save(Person person) {
        peopleRepo.save(person);
    }

}