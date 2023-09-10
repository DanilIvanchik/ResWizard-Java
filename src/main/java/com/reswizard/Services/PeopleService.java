package com.reswizard.Services;

import com.reswizard.Models.Person;
import com.reswizard.Repositories.PeopleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepo peopleRepo;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public PeopleService(PeopleRepo peopleRepo, PasswordEncoder passwordEncoder) {
        this.peopleRepo = peopleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean findDuplicate(String name){
        return peopleRepo.findByUsername(name).isPresent();
    }

    public boolean findUserByEmail(String email){
        return peopleRepo.findByEmail(email).isPresent();
    }

    public Person findUserByUsername(String name){
        if (peopleRepo.findByUsername(name).isPresent()){
            Optional<Person> person = peopleRepo.findByUsername(name);
            return person.get();
        }else{
            return null;
        }
    }

    public boolean validatePassword(String password){
        if (peopleRepo.findByPassword(passwordEncoder.encode(password)).isPresent()){
            return true;
        }else{
            return false;
        }
    }

    public Person findPersonById(int id){
        return peopleRepo.findPersonById(id).get();
    }

    @Transactional
    public void save(Person person) {
        peopleRepo.save(person);
    }
}