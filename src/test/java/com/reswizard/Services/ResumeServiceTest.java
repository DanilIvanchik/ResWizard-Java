package com.reswizard.Services;

import com.reswizard.Models.Resume;
import com.reswizard.Models.User;
import com.reswizard.Repositories.ResumeRepo;
import com.reswizard.Util.Languages;
import io.undertow.security.api.SecurityContext;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class ResumeServiceTest {

    @Autowired
    private ResumeService resumeService;
    @MockBean
    private ResumeRepo resumeRepository;
    @MockBean
    private UserService userService;
    @Value("${upload.file.path}")
    private String uploadPath;

    @Test
    void save() {
        Resume resume = mock(Resume.class);
        resumeService.save(resume);
        Mockito.verify(resumeRepository, Mockito.times(1)).save(resume);
    }

    @Test
    void findResumeById() {
        Resume resume = mock(Resume.class);
        when(resumeRepository.findResumeById(1)).thenReturn(resume);
        resumeService.findResumeById(1);
        Mockito.verify(resumeRepository, Mockito.times(1)).findResumeById(1);
    }

    @Test
    void handleResumeFileDownload() {
    }

    @Test
    void handleResumeFileUpload() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("testUser");

        User user = new User();
        user.setId(1);

        Resume existingResume = new Resume("existingResume.pdf", "/path/to/existing/", user, Languages.ENGLISH);

        when(userService.findUserByUsername("testUser")).thenReturn(user);
        when(resumeRepository.findAllByLanguageAndOwner_Id(Languages.ENGLISH, user.getId())).thenReturn(existingResume);

        when(mockFile.getOriginalFilename()).thenReturn("testResume.pdf");
        when(mockFile.isEmpty()).thenReturn(false);
//        when(user.getResumes()).thenReturn(Collections.singletonList(existingResume));

//        resumeService.handleResumeFileUpload(mockFile, uploadPath, Languages.ENGLISH);

    }

    @Test
    void deleteUsersResumeFromEditPage() {
    }

    @Test
    void getUsersExistingResumes() {
    }

    @Test
    void isAllResumesExist() {
    }

    @Test
    void isResumeExist() {
    }

    @Test
    void getFileSizeMegabytes() {
    }
}