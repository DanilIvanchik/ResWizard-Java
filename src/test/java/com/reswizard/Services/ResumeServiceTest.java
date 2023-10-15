package com.reswizard.Services;

import com.reswizard.Models.Resume;
import com.reswizard.Models.User;
import com.reswizard.Repositories.ResumeRepo;
import com.reswizard.Util.IncorrectAvatarFormatException;
import com.reswizard.Util.IncorrectResumeFormatException;
import com.reswizard.Util.Languages;
import io.undertow.security.api.SecurityContext;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void handleResumeFileDownload() throws FileNotFoundException {
        String fileName = "test.pdf";

        MockHttpServletResponse response = new MockHttpServletResponse();

        resumeService.handleResumeFileDownload(fileName, response, uploadPath);

        assertEquals("application/pdf", response.getContentType());
        assertEquals("attachment; filename=test.pdf", response.getHeader("Content-Disposition"));
        assertEquals("binary", response.getHeader("Content-Transfer-Encoding"));
    }

    @Test
    void handleResumeFileUpload() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);

        SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("testUser");
        when(mockFile.getOriginalFilename()).thenReturn("mockFile.png");

        User user = new User();
        user.setId(1);
        user.setResumes(new ArrayList<>());

        Resume existingResume = new Resume("existingResume.pdf", uploadPath, user, Languages.ENGLISH);
        user.getResumes().add(existingResume);

        when(userService.findUserByUsername("testUser")).thenReturn(user);
        when(resumeRepository.findAllByLanguageAndOwner_Id(Languages.ENGLISH, 1)).thenReturn(existingResume);
        when(resumeService.generateUniqueFileName(mockFile.getOriginalFilename())).thenReturn("testResume.pdf");

        when(mockFile.getOriginalFilename()).thenReturn("testResume.pdf");
        when(mockFile.isEmpty()).thenReturn(false);
//        Resume resume = new Resume("testResume.pdf", uploadPath, user, Languages.ENGLISH);
        resumeService.handleResumeFileUpload(mockFile, uploadPath, Languages.ENGLISH);
//        Mockito.verify(resumeService, Mockito.times(1)).updateUserResumes(user, existingResume, resume);

    }

    @Test
    void handleFailedResumeFileUpload() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("mockFile.jpg");
        assertThrows(IncorrectResumeFormatException.class, () -> resumeService.handleResumeFileUpload(mockFile, uploadPath, Languages.ENGLISH));
    }

    @Test
    void deleteUsersResumeFromEditPage() {
        Resume resume = new Resume("resume1.pdf", uploadPath, new User(), Languages.ENGLISH);

        User user = Mockito.mock(User.class);

        List<Resume> resumes = new ArrayList<>();
        resumes.add(resume);

        when(user.getResumes()).thenReturn(resumes);

        doAnswer(invocation -> {
            Resume resumeToRemove = invocation.getArgument(0);

            resumes.remove(resumeToRemove);

            return null;
        }).when(user).getResumes();
    }


    @Test
    void isAllResumesExist() {
        List<Resume> resumes = new ArrayList<>();
        resumes.add(new Resume("resume1.pdf", uploadPath, new User(), Languages.ENGLISH));
        resumes.add(new Resume("resume2.pdf", uploadPath, new User(), Languages.ENGLISH));

        String path = uploadPath;

        ResumeService resumeService = mock(ResumeService.class);

        when(resumeService.isResumeExist("resume1.pdf", path)).thenReturn(true);
        when(resumeService.isResumeExist("resume2.pdf", path)).thenReturn(false);

        boolean result = resumeService.isAllResumesExist(resumes, path);

        assertFalse(result);
    }

    @Test
    void isResumeExist() {
        File tempDirectory = new File(uploadPath);
        tempDirectory.mkdir();

        try {
            File file1 = new File(tempDirectory, "resume1.pdf");
            File file2 = new File(tempDirectory, "resume2.pdf");
            File file3 = new File(tempDirectory, "resume3.pdf");

            file1.createNewFile();
            file2.createNewFile();

            assertTrue(resumeService.isResumeExist("resume1.pdf", tempDirectory.getAbsolutePath()));
            assertTrue(resumeService.isResumeExist("resume2.pdf", tempDirectory.getAbsolutePath()));

            assertFalse(resumeService.isResumeExist("resume4.pdf", tempDirectory.getAbsolutePath()));

            file1.delete();
            file2.delete();
            file3.delete();

            assertFalse(resumeService.isResumeExist("resume1.pdf", tempDirectory.getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            tempDirectory.delete();
        }
    }

    @Test
    void getFileSizeMegabytes() {
        MockMultipartFile testFile = new MockMultipartFile("testFile", "test.txt", "text/plain", new byte[1024 * 1024]);

        Double result = resumeService.getFileSizeMegabytes(testFile);

        assertEquals(1.0, result, 0.01);
    }
}