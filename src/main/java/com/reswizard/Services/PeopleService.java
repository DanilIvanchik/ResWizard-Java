package com.reswizard.Services;

import com.reswizard.Repositories.PeopleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PeopleService {
    private final PeopleRepo peopleRepo;


    @Autowired
    public PeopleService(PeopleRepo peopleRepo) {
        this.peopleRepo = peopleRepo;
    }

    public boolean findDuplicate(String name){
        if (peopleRepo.findByUsername(name).isPresent()){
            return true;
        }else{
            return false;
        }
    }
}