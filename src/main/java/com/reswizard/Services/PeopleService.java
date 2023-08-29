package com.reswizard.Services;

import com.reswizard.Repositories.PeopleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PeopleService {
    private final PeopleRepo peopleRepo;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public PeopleService(PeopleRepo peopleRepo, PasswordEncoder passwordEncoder) {
        this.peopleRepo = peopleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean findDuplicate(String name){
        if (peopleRepo.findByUsername(name).isPresent()){
            return true;
        }else{
            return false;
        }
    }

    public boolean findUserByEmail(String email){
        if (peopleRepo.findByEmail(email).isPresent()){
            return true;
        }else{
            return false;
        }
    }

    public boolean findUserByName(String name){
        if (peopleRepo.findByUsername(name).isPresent()){
            return true;
        }else{
            return false;
        }
    }

    public boolean validatePassword(String password){
        if (peopleRepo.findByPassword(passwordEncoder.encode(password)).isPresent()){
            return true;
        }else{
            return false;
        }
    }
}