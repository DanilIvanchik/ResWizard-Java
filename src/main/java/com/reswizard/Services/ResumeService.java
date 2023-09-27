package com.reswizard.Services;

import com.reswizard.Models.Person;
import com.reswizard.Models.Resume;
import com.reswizard.Repositories.ResumeRepo;
import com.reswizard.Util.IncorrectResumeFormatException;
import com.reswizard.Util.Languages;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
@Transactional(readOnly = true)
public class ResumeService {

    private final ResumeRepo resumeRepository;
    private final PeopleService peopleService;
    private static final Logger logger = Logger.getGlobal();

    @Autowired
    public ResumeService(ResumeRepo resumeRepository, PeopleService peopleService) {
        this.resumeRepository = resumeRepository;
        this.peopleService = peopleService;
    }

    // Find all resumes of a person by their ID
    public List<Resume> findAllPersonResumes(int id) {
        return resumeRepository.findAllByOwner_Id(id);
    }

    // Save a resume
    @Transactional
    public void saveResume(Resume resume) {
        resumeRepository.save(resume);
        logger.log(Level.INFO, "Saved resume: " + resume.getTitle());
    }

    // Save a resume (overloaded method)
    @Transactional
    public void save(Resume resume) {
        resumeRepository.save(resume);
        logger.log(Level.INFO, "Saved resume: " + resume.getTitle());
    }

    // Find a resume by its ID
    public Resume findResumeById(int id) {
        return resumeRepository.findResumeById(id);
    }

    // Handle downloading a resume file
    @Transactional
    public void handleResumeFileDownload(String fileName, HttpServletResponse response, String filePath) {
        logger.log(Level.INFO, "Downloading file: " + fileName);

        // Set response content type based on file extension
        if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            response.setContentType("application/msword");
        } else if (fileName.endsWith(".pdf")) {
            response.setContentType("application/pdf");
        }

        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setHeader("Content-Transfer-Encoding", "binary");

        try (FileInputStream fileInputStream = new FileInputStream(filePath + fileName);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream())) {

            int len;
            byte[] buf = new byte[1024];
            while ((len = fileInputStream.read(buf)) > 0) {
                bufferedOutputStream.write(buf, 0, len);
            }
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.log(Level.INFO, "Downloaded file: " + fileName + " successfully.");
    }

    // Handle uploading a resume file
    @Transactional
    public void handleResumeFileUpload(MultipartFile file, String uploadPath, Languages selectedLanguage) throws IOException {
        logger.log(Level.INFO, "Uploading file: " + file.getOriginalFilename());

        if (file == null || file.isEmpty()) {
            return;
        }

        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());

        if (!isValidResumeFormat(uniqueFileName)) {
            throw new IncorrectResumeFormatException("Invalid file format. Only PDF and DOCX are allowed.");
        }

        String filePath = uploadPath + uniqueFileName;

        file.transferTo(new File(filePath));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Person person = peopleService.findUserByUsername(username);

        Resume existingResume = resumeRepository.findAllByLanguageAndOwner_Id(selectedLanguage, person.getId());
        Resume resume = new Resume(uniqueFileName, uploadPath, person, selectedLanguage);

        if (existingResume != null) {
            deleteResumeFromStorage(uploadPath, existingResume.getTitle());
            resume.setId(existingResume.getId());
            save(resume);
            updatePersonResumes(person, existingResume, resume);
        } else {
            save(resume);
            person.getResumes().add(resume);
            peopleService.save(person);
        }

        logger.log(Level.INFO, "File upload completed successfully: " + file.getOriginalFilename());
    }

    // Check if a file format is valid
    private boolean isValidResumeFormat(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return fileExtension.equals("pdf") || fileExtension.equals("docx") || fileExtension.equals("doc");
    }

    // Delete a resume file from storage
    private void deleteResumeFromStorage(String uploadPath, String fileName) {
        File file = new File(uploadPath + fileName);
        if (file.delete()) {
            logger.log(Level.INFO, "Deleted resume: " + uploadPath + fileName);
        } else {
            logger.log(Level.INFO, "File not found: " + uploadPath + fileName);
        }
    }

    // Delete a person's resume from the edit page
    @Transactional
    public void deletePersonResumeFromEditPage(Integer resumeId, String uploadPath) {
        Resume resume = resumeRepository.findResumeById(resumeId);
        resumeRepository.deleteResumeById(resumeId);
        deleteResumeFromStorage(uploadPath, resume.getTitle());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Person person = peopleService.findUserByUsername(username);
        person.getResumes().remove(resume);
        peopleService.save(person);
    }

    // Generate a unique file name for a resume
    private String generateUniqueFileName(String originalFileName) {
        String uidFile = UUID.randomUUID().toString();
        int lastDotIndex = originalFileName.lastIndexOf(".");
        String extension = (lastDotIndex >= 0) ? originalFileName.substring(lastDotIndex) : "";
        return uidFile + extension;
    }

    // Update a person's resumes
    private void updatePersonResumes(Person person, Resume oldResume, Resume newResume) {
        person.getResumes().remove(oldResume);
        person.getResumes().add(newResume);
        peopleService.save(person);
        logger.log(Level.INFO, "Updated resume: " + newResume.getTitle());
    }

    // Get a person's existing resumes and delete those that don't exist in storage
    @Transactional
    public List<Resume> getPersonsExistedResumes(List<Resume> resumes, String path, Person currentPerson) {
        List<Resume> newResumeList = new ArrayList<>();
        for (Resume r : resumes) {
            if (isResumeExist(r.getTitle(), path)) {
                newResumeList.add(r);
            } else {
                resumeRepository.deleteResumeById(r.getId());
            }
        }
        currentPerson.setResumes(newResumeList);
        peopleService.save(currentPerson);
        return newResumeList;
    }

    // Check if all resumes exist in storage
    public boolean isAllResumesExist(List<Resume> resumes, String path) {
        for (Resume r : resumes) {
            if (!isResumeExist(r.getTitle(), path)) {
                return false;
            }
        }
        return true;
    }

    // Check if a specific resume exists in storage
    private boolean isResumeExist(String title, String path) {
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (files != null) {
            HashSet<String> fileNames = new HashSet<>();

            for (File file : files) {
                fileNames.add(file.getName());
            }

            return fileNames.contains(title);
        }

        return false;
    }
}
