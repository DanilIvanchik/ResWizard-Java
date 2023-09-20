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
        if (!isValidResumeFormat(uniqueFileName)) {
            throw new IncorrectAvatarFormatException("Invalid file format. Only PDF is allowed.");
        }
        String filePath = uploadPath + uniqueFileName;

        file.transferTo(new File(filePath));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Person> person = peopleRepo.findByUsername(username);
        if (person.get().getAvatarTitle() != null){
            deleteOldAvatar(uploadPath, person.get().getAvatarTitle());
        }
        person.get().setAvatarTitle(uniqueFileName);
        peopleRepo.save(person.get());
    }

    private void deleteOldAvatar(String uploadPath, String fileName){
        File file = new File(uploadPath+fileName);
        if(file.delete()){
            System.out.println(uploadPath+fileName+" file deleted");
        }else{
            System.out.println("File "+uploadPath+fileName+" has not been found");
        }
    }

    private boolean isValidResumeFormat(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return fileExtension.equals("png");
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