package com.reswizard.Services;

import com.reswizard.Models.Person;
import com.reswizard.Repositories.PeopleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    public final PeopleRepo peopleRepo;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public RegistrationService(PeopleRepo peopleRepo, PasswordEncoder passwordEncoder) {
        this.peopleRepo = peopleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(Person person){
        String password = person.getPassword();
        person.setPassword(passwordEncoder.encode(password));
        person.setRole("ROLE_USER");
        person.setAvatarTitle("defaultAvatar.png");
        peopleRepo.save(person);
    }
}
