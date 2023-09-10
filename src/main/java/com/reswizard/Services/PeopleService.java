package com.reswizard.Services;

import com.reswizard.Models.Person;
import com.reswizard.Repositories.PeopleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepo peopleRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PeopleService(
            PeopleRepo peopleRepo,
            PasswordEncoder passwordEncoder) {
        this.peopleRepo = peopleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean findUserByEmail(String email) {
        return peopleRepo.findByEmail(email).isPresent();
    }

    public Person findUserByUsername(String name) {
        return peopleRepo.findByUsername(name).orElse(null);
    }

    public Person findUserById(int id) {
        return peopleRepo.findById(id).orElse(null);
    }

    public boolean isValidPassword(String password) {
        return peopleRepo
                .findByPassword(passwordEncoder.encode(password))
                .isPresent();
    }

    public boolean isDuplicateName(String name) {
        return findUserByUsername(name) != null;
    }

    @Transactional
    public void save(Person person) {
        peopleRepo.save(person);
    }
}