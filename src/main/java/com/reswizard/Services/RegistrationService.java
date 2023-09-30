package com.reswizard.Services;

import com.reswizard.Models.Person;
import com.reswizard.Repositories.PeopleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.UUID;

@Service
public class RegistrationService {

    public final PeopleRepo peopleRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailSender mailSender;


    @Autowired
    public RegistrationService(PeopleRepo peopleRepo, PasswordEncoder passwordEncoder, MailSender mailSender) {
        this.peopleRepo = peopleRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Transactional
    public void register(Person person) {
        String password = person.getPassword();
        person.setPassword(passwordEncoder.encode(password));
        person.setRole("ROLE_USER");
        person.setAvatarTitle("defaultAvatar.png");
        person.setActivationCode(UUID.randomUUID().toString());
        peopleRepo.save(person);

        if (!StringUtils.isEmpty(person.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to ResWizard! Please, visit next link to activate tour account : http://localhost:8080/auth/activate/%s",
                    person.getUsername(),
                    person.getActivationCode()
            );

            mailSender.sendMail(person.getEmail(), "Activation code", message);
        }

    }
}