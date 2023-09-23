package com.reswizard.Repositories;

import com.reswizard.Models.Person;
import com.reswizard.Models.Resume;
import com.reswizard.Util.Languages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepo extends JpaRepository<Resume, Integer> {

    List<Resume> findAllByOwner_Id(int id);
    Resume findResumeById(int id);
    Resume findAllByLanguageAndOwner_Id(Languages language, Integer id);
    Resume findResumeByTitle(String title);
    void deleteResumeById(int id);
}
