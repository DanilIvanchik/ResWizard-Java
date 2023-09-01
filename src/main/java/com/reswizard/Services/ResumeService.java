package com.reswizard.Services;

import com.reswizard.Repositories.ResumeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResumeService {

    private final ResumeRepo resumeRepository;

    @Autowired
    public ResumeService(ResumeRepo resumeRepository) {
        this.resumeRepository = resumeRepository;
    }
}
