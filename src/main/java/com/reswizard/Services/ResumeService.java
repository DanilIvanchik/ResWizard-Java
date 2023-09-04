package com.reswizard.Services;

import com.reswizard.Models.Resume;
import com.reswizard.Repositories.ResumeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ResumeService {

    private final ResumeRepo resumeRepository;

    @Autowired
    public ResumeService(ResumeRepo resumeRepository) {
        this.resumeRepository = resumeRepository;
    }


    public List<Resume> findAllPersonResumes(int id){
        return resumeRepository.findAllByOwner_Id(id);
    }

    @Transactional
    public void saveResume(Resume resume){
        resumeRepository.save(resume);
    }


}
