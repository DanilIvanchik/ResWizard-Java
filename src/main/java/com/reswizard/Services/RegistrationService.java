package com.reswizard.Services;

import com.reswizard.Models.User;
import com.reswizard.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.UUID;

@Service
public class RegistrationService {

    public final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailSender mailSender;


    @Autowired
    public RegistrationService(UserRepo userRepo, PasswordEncoder passwordEncoder, MailSender mailSender) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    /**
     * Registers a new user by saving their information to the database.
     *
     * @param user The User object to be registered.
     */
    @Transactional
    public void register(User user) {
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER");
        user.setAvatarTitle("defaultAvatar.png");
        user.setActivationCode(UUID.randomUUID().toString());
        user.setIsInRecovering(false);
        user.setResumePassKey(passwordEncoder.encode(user.getUsername()).replace('/', 'w'));
        userRepo.save(user);

        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to ResWizard! Please, visit next link to activate your account : http://localhost:8080/auth/activate/%s",
                    user.getUsername(),
                    user.getActivationCode()
            );

            mailSender.sendMail(user.getEmail(), "Activation code", message);
        }

    }
}