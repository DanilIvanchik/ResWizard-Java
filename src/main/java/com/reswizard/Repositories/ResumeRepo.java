package com.reswizard.Repositories;

import com.reswizard.Models.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepo extends JpaRepository<Resume, Integer> {

    List<Resume> findAllByOwner_Id(int id);

}
