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
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ResumeService {

    private final ResumeRepo resumeRepository;
    private final PeopleService peopleService;

    @Autowired
    public ResumeService(ResumeRepo resumeRepository, PeopleService peopleService) {
        this.resumeRepository = resumeRepository;
        this.peopleService = peopleService;
    }


    public List<Resume> findAllPersonResumes(int id){
        return resumeRepository.findAllByOwner_Id(id);
    }

    @Transactional
    public void saveResume(Resume resume){
        resumeRepository.save(resume);
    }

    @Transactional
    public void save(Resume resume){
        resumeRepository.save(resume);
    }

    public Resume findResumeById(int id){
        return resumeRepository.findResumeById(id);
    }

    public void handleResumeFileDownload(String fileName, HttpServletResponse response, String uploadPath){
        if (fileName.indexOf(".doc")>-1) response.setContentType("application/msword");
        if (fileName.indexOf(".docx")>-1) response.setContentType("application/msword");
        if (fileName.indexOf(".pdf")>-1) response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" +fileName);
        response.setHeader("Content-Transfer-Encoding", "binary");
        try(FileInputStream fileInputStream = new FileInputStream(uploadPath+fileName);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());) {
            int len;
            byte[] buf = new byte[1024];
            while((len = fileInputStream.read(buf)) > 0) {
                bufferedOutputStream.write(buf,0,len);
            }
            response.flushBuffer();
        }
        catch(IOException e) {
            e.printStackTrace();

        }
    }

    @Transactional
    public void handleResumeFileUpload(MultipartFile file, String uploadPath, Languages selectedLanguage) throws IOException {
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
            deleteResume(uploadPath, existingResume.getTitle());
            resume.setId(existingResume.getId());
            save(resume);
            updatePersonResumes(person, existingResume, resume);
        } else {
            save(resume);
            person.getResumes().add(resume);
            peopleService.save(person);
        }
    }

    private boolean isValidResumeFormat(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return fileExtension.equals("pdf") || fileExtension.equals("docx")|| fileExtension.equals("doc");
    }

    private void deleteResume(String uploadPath, String fileName){
        File file = new File(uploadPath+fileName);
        if(file.delete()){
            System.out.println(uploadPath+fileName+" file deleted");
        }else{
            System.out.println("File "+uploadPath+fileName+" has not been found");
        }
    }

    @Transactional
    public void deletePersonResumeFromSettingsPage(Integer resumeId, String uploadPath){
        Resume resume = resumeRepository.findResumeById(resumeId);
        resumeRepository.deleteResumeById(resumeId);
        deleteResume(uploadPath, resume.getTitle());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Person person = peopleService.findUserByUsername(username);
        person.getResumes().remove(resume);
        peopleService.save(person);

    }

    private String generateUniqueFileName(String originalFileName) {
        String uidFile = UUID.randomUUID().toString();
        int lastDotIndex = originalFileName.lastIndexOf(".");
        String extension = (lastDotIndex >= 0) ? originalFileName.substring(lastDotIndex) : "";
        return uidFile + extension;
    }

    private void updatePersonResumes(Person person, Resume oldResume, Resume newResume) {
        person.getResumes().remove(oldResume);
        person.getResumes().add(newResume);
        peopleService.save(person);
    }


}
