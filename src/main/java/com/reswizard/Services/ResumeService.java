package com.reswizard.Services;

import com.reswizard.Models.User;
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
    private final UserService userService;
    private static final Logger logger = Logger.getGlobal();

    @Autowired
    public ResumeService(ResumeRepo resumeRepository, UserService userService) {
        this.resumeRepository = resumeRepository;
        this.userService = userService;
    }

    /**
     * Saves a resume to the database.
     *
     * @param resume The Resume object to be saved.
     */
    @Transactional
    public void save(Resume resume) {
        resumeRepository.save(resume);
        logger.log(Level.INFO, "Saved resume: " + resume.getTitle());
    }

    /**
     * Finds a resume by its ID.
     *
     * @param id The ID of the resume to be found.
     * @return Resume object with the specified ID.
     */
    public Resume findResumeById(int id) {
        return resumeRepository.findResumeById(id);
    }

    /**
     * Handles the download of a resume file and sends it to the HttpServletResponse.
     *
     * @param fileName   The name of the file to be downloaded.
     * @param response   The HttpServletResponse object.
     * @param filePath   The path to the directory where the file is stored.
     */
    @Transactional
    public void handleResumeFileDownload(String fileName, HttpServletResponse response, String filePath) {
        logger.log(Level.INFO, "Downloading file: " + fileName);

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


    /**
     * Handles the upload of a resume file.
     *
     * @param file            The MultipartFile containing the resume file.
     * @param uploadPath      The path to the directory where uploaded files are stored.
     * @param selectedLanguage The language of the resume.
     * @throws IOException If an I/O error occurs during file transfer.
     */
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
        User user = userService.findUserByUsername(username);

        Resume existingResume = resumeRepository.findAllByLanguageAndOwner_Id(selectedLanguage, user.getId());
        Resume resume = new Resume(uniqueFileName, uploadPath, user, selectedLanguage);

        if (existingResume != null) {
            deleteResumeFromStorage(uploadPath, existingResume.getTitle());
            resume.setId(existingResume.getId());
            save(resume);
            updateUserResumes(user, existingResume, resume);
        } else {
            save(resume);
            user.getResumes().add(resume);
            userService.save(user);
        }

        logger.log(Level.INFO, "File upload completed successfully: " + file.getOriginalFilename());
    }

    /**
     * Checks the format of the resume uploaded by the user.
     *
     * @param fileName  Name of the uploaded file.
     */
    private boolean isValidResumeFormat(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return fileExtension.equals("pdf") || fileExtension.equals("docx") || fileExtension.equals("doc");
    }

    /**
     * Deletes a resume from storage.
     *
     * @param fileName   The name of the file.
     * @param uploadPath The path to the directory where uploaded files are stored.
     */
    private void deleteResumeFromStorage(String uploadPath, String fileName) {
        File file = new File(uploadPath + fileName);
        if (file.delete()) {
            logger.log(Level.INFO, "Deleted resume: " + uploadPath + fileName);
        } else {
            logger.log(Level.INFO, "File not found: " + uploadPath + fileName);
        }
    }

    /**
     * Deletes a resume file from storage and the database.
     *
     * @param resumeId   The ID of the resume to be deleted.
     * @param uploadPath The path to the directory where uploaded files are stored.
     */
    @Transactional
    public void deleteUsersResumeFromEditPage(Integer resumeId, String uploadPath) {
        Resume resume = resumeRepository.findResumeById(resumeId);
        resumeRepository.deleteResumeById(resumeId);
        deleteResumeFromStorage(uploadPath, resume.getTitle());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findUserByUsername(username);
        user.getResumes().remove(resume);
        userService.save(user);
    }

    /**
     * Generates a unique file name by appending a UUID to the original file name.
     *
     * @param originalFileName The original file name.
     * @return The generated unique file name.
     */
    private String generateUniqueFileName(String originalFileName) {
        String uidFile = UUID.randomUUID().toString();
        int lastDotIndex = originalFileName.lastIndexOf(".");
        String extension = (lastDotIndex >= 0) ? originalFileName.substring(lastDotIndex) : "";
        return uidFile + extension;
    }

    /**
     * Updates a user's resumes by replacing an old resume with a new one.
     *
     * @param user      The User to whom the resumes belong.
     * @param oldResume The old Resume to be replaced.
     * @param newResume The new Resume to be added.
     */
    private void updateUserResumes(User user, Resume oldResume, Resume newResume) {
        user.getResumes().remove(oldResume);
        user.getResumes().add(newResume);
        userService.save(user);
        logger.log(Level.INFO, "Updated resume: " + newResume.getTitle());
    }

    /**
     * Retrieves a list of resumes that exist in storage and updates the user's resumes accordingly.
     *
     * @param resumes     The list of resumes to be checked.
     * @param path        The path to the directory where uploaded files are stored.
     * @param currentUser The User to whom the resumes belong.
     * @return A list of existing Resume objects.
     */
    @Transactional
    public List<Resume> getUsersExistingResumes(List<Resume> resumes, String path, User currentUser) {
        List<Resume> newResumeList = new ArrayList<>();
        for (Resume r : resumes) {
            if (isResumeExist(r.getTitle(), path)) {
                newResumeList.add(r);
            } else {
                resumeRepository.deleteResumeById(r.getId());
            }
        }
        currentUser.setResumes(newResumeList);
        userService.save(currentUser);
        return newResumeList;
    }

    /**
     * Checks if all resumes in a list exist in storage.
     *
     * @param resumes The list of resumes to be checked.
     * @param path    The path to the directory where uploaded files are stored.
     * @return True if all resumes exist, false otherwise.
     */
    public boolean isAllResumesExist(List<Resume> resumes, String path) {
        for (Resume r : resumes) {
            if (!isResumeExist(r.getTitle(), path)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a specific resume file exists in storage.
     *
     * @param title The name of the resume file.
     * @param path  The path to the directory where uploaded files are stored.
     * @return True if the resume file exists, false otherwise.
     */
    public boolean isResumeExist(String title, String path) {
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

    /**
     * Calculates and returns the size of a file in megabytes.
     *
     * @param file The MultipartFile representing the file.
     * @return The file size in megabytes.
     */
    public Double getFileSizeMegabytes(MultipartFile file) {
        return (double) file.getSize()/(1024*1024);
    }
}
