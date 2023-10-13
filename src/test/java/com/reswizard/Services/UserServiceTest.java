package com.reswizard.Services;

import com.reswizard.Models.User;
import com.reswizard.Repositories.UserRepo;
import com.reswizard.Util.IncorrectAvatarFormatException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private MailSender mailSender;
    @Value("${upload.file.path}")
    private String uploadPath;

    @Test
    public void testIsUsernamePresent() {
        when(userRepo.findByUsername("existingUsername")).thenReturn(Optional.of(new User()));
        when(userRepo.findByUsername("nonExistingUsername")).thenReturn(Optional.empty());

        assertTrue(userService.isUsernamePresent("existingUsername"));
        assertFalse(userService.isUsernamePresent("nonExistingUsername"));
    }

    @Test
    public void testIsUserPresentByEmail() {
        when(userRepo.findByEmail("existingEmail")).thenReturn(Optional.of(new User()));
        when(userRepo.findByEmail("nonExistingEmail")).thenReturn(Optional.empty());

        assertTrue(userService.isUserPresentByEmail("existingEmail"));
        assertFalse(userService.isUserPresentByEmail("nonExistingEmail"));
    }

    @Test
    public void testIsUsersAccountActivated(){
        User user = new User();
        user.setActivationCode("code");
        assertFalse(userService.isActiveUser(user.getActivationCode()));
        when(userRepo.findByActivationCode("code")).thenReturn(Optional.of(new User()));
        assertTrue(userService.isActiveUser(user.getActivationCode()));
    }

    @Test
    public void testSuccessfulEmailRecovering(){
        User user = new User();
        user.setEmail("test@email.com");
        user.setRole("ROLE_USER");
        when(userRepo.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        userService.sendRecoveringEmail(user.getEmail());
        Mockito.verify(mailSender,Mockito.times(1)).sendMail(
                ArgumentMatchers.eq(user.getEmail()),
                ArgumentMatchers.eq("Password recovering"),
                ArgumentMatchers.contains("Welcome to ResWizard!"));
        Mockito.verify(userRepo, Mockito.times(1)).save(user);
    }

    @Test
    public void testFailedEmailRecovering(){
        User user = new User();
        user.setEmail("test@email.com");
        user.setRole("ROLE_USER");
        userService.sendRecoveringEmail(user.getEmail());
        Mockito.verify(mailSender,Mockito.times(0)).sendMail(
                ArgumentMatchers.eq(user.getEmail()),
                ArgumentMatchers.eq("Password recovering"),
                ArgumentMatchers.contains("Welcome to ResWizard!"));
        Mockito.verify(userRepo, Mockito.times(0)).save(user);
    }

    @Test
    public void testHandleAvatarFileUpload() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.png");

        SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("testUser");
        User user = mock(User.class);
        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(user.getAvatarTitle()).thenReturn("title.png");

        userService.handleAvatarFileUpload(mockFile, uploadPath);
        Mockito.verify(user, Mockito.times(2)).getAvatarTitle();
        Mockito.verify(userRepo, Mockito.times(1)).save(user);
    }

    @Test
    public void testFirstAvatarFileUpload() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.png");

        SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("testUser");
        User user = mock(User.class);
        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(user.getAvatarTitle()).thenReturn("defaultAvatar.png");

        userService.handleAvatarFileUpload(mockFile, uploadPath);
        Mockito.verify(user, Mockito.times(1)).getAvatarTitle();
        Mockito.verify(userRepo, Mockito.times(1)).save(user);
    }

    @Test
    public void testFailedHandleAvatarFileUpload() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        assertThrows(IncorrectAvatarFormatException.class, () -> userService.handleAvatarFileUpload(mockFile, uploadPath));
    }
}