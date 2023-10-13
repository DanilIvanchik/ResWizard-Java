package com.reswizard.Services;

import com.reswizard.Models.User;
import com.reswizard.Repositories.UserRepo;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.management.relation.Role;
import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest
class RegistrationServiceTest {
    @Autowired
    private RegistrationService registrationService;
    @MockBean
    public UserRepo userRepo;
    @MockBean
    private MailSender mailSender;
    @Test
    void register() {
        User user = new User();
        user.setUsername("Test");
        user.setPassword("asccascs");
        user.setEmail("email@gmail.com");
        registrationService.register(user);

        Assert.assertNotNull(user.getActivationCode());
        Assert.assertEquals("ROLE_USER", user.getRole());
        Mockito.verify(userRepo, Mockito.times(1)).save(user);
        Mockito.verify(mailSender, Mockito.times(1)).sendMail(
                ArgumentMatchers.eq(user.getEmail()),
                ArgumentMatchers.eq("Activation code"),
                ArgumentMatchers.contains("Welcome to ResWizard!"));
    }
}
