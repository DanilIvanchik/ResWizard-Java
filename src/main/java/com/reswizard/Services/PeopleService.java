package com.reswizard.Services;

import com.reswizard.Models.Person;
import com.reswizard.Repositories.PeopleRepo;
import com.reswizard.Util.IncorrectAvatarFormatException;
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
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepo peopleRepo;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = Logger.getGlobal();

    @Autowired
    public PeopleService(PeopleRepo peopleRepo, PasswordEncoder passwordEncoder) {
        this.peopleRepo = peopleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // Check if a username is a duplicate
    public boolean isDuplicate(String name) {
        return peopleRepo.findByUsername(name).isPresent();
    }

    // Check if a user is present by email
    public boolean isUserPresentByEmail(String email) {
        return peopleRepo.findByEmail(email).isPresent();
    }

    // Find a user by username
    public Person findUserByUsername(String name) {
        logger.log(Level.INFO, "Finding user by username: " + name);
        Optional<Person> person = peopleRepo.findByUsername(name);
        return person.orElse(null);
    }

    // Handle uploading an avatar file
    @Transactional
    public void handleAvatarFileUpload(MultipartFile file, String uploadPath) throws IOException {
        logger.log(Level.INFO, "Uploading avatar file: " + file.getOriginalFilename());

        if (file == null || file.isEmpty()) {
            return;
        }

        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());
        if (!isValidAvatarFormat(uniqueFileName)) {
            throw new IncorrectAvatarFormatException("Invalid file format. Only PNG is allowed.");
        }
        String filePath = uploadPath + uniqueFileName;

        file.transferTo(new File(filePath));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Person> personOptional = peopleRepo.findByUsername(username);

        if (personOptional.isPresent()) {
            Person person = personOptional.get();
            System.out.println(person.getAvatarTitle());
            if (!person.getAvatarTitle().equals("defaultAvatar.png")) {
                System.out.println("!");
                deleteOldAvatar(uploadPath, person.getAvatarTitle());
            }
            person.setAvatarTitle(uniqueFileName);
            peopleRepo.save(person);
        }

        logger.log(Level.INFO, "Avatar file upload completed successfully: " + file.getOriginalFilename());
    }

    // Delete an old avatar file
    private void deleteOldAvatar(String uploadPath, String fileName) {
        File file = new File(uploadPath + fileName);
        if (file.delete()) {
            logger.log(Level.INFO, "Deleted old avatar: " + uploadPath + fileName);
        } else {
            logger.log(Level.INFO, "Old avatar file not found: " + uploadPath + fileName);
        }
    }

    // Check if the file format is valid for avatars
    private boolean isValidAvatarFormat(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return fileExtension.equals("png");
    }

    // Generate a unique file name for uploaded avatars
    private String generateUniqueFileName(String originalFileName) {
        String uidFile = UUID.randomUUID().toString();
        int lastDotIndex = originalFileName.lastIndexOf(".");
        String extension = (lastDotIndex >= 0) ? originalFileName.substring(lastDotIndex) : "";
        return uidFile + extension;
    }

    // Check if a password is valid
    public boolean isPasswordValid(String password) {
        return peopleRepo.findByPassword(passwordEncoder.encode(password)).isPresent();
    }

    // Find a person by their ID
    public Person findPersonById(int id) {
        logger.log(Level.INFO, "Finding person by ID: " + id);
        return peopleRepo.findPersonById(id).orElse(null);
    }

    // Save a person
    @Transactional
    public void save(Person person) {
        peopleRepo.save(person);
        logger.log(Level.INFO, "Saved person: " + person.getUsername());
    }

    // Get the currently authenticated person
    public Person getCurrentPerson() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        logger.log(Level.INFO, "Getting current authenticated person: " + username);
        Optional<Person> person = peopleRepo.findByUsername(username);
        return person.orElse(null);
    }

    /**
     * Checks if the length of the given message is valid.
     *
     * @param message The string representing the message to be checked.
     * @return true if the message length is greater than 500 characters; otherwise, returns false.
     */
    public boolean isMessageLengthValid(String message){
        return message.length() > 500;
    }
}