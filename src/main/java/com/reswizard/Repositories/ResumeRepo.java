package com.reswizard.Repositories;

import com.reswizard.Models.Resume;
import com.reswizard.Util.Languages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepo extends JpaRepository<Resume, Integer> {

    /**
     * Retrieve a resume by its ID.
     *
     * @param id The ID of the resume to retrieve.
     * @return The resume with the specified ID, or null if not found.
     */
    Resume findResumeById(int id);

    /**
     * Retrieve a resume by its language and owner's ID.
     *
     * @param language The language of the resume.
     * @param id The ID of the owner.
     * @return The resume with the specified language and owner, or null if not found.
     */
    Resume findAllByLanguageAndOwner_Id(Languages language, Integer id);

    /**
     * Delete a resume by its ID.
     *
     * @param id The ID of the resume to delete.
     */
    void deleteResumeById(int id);
}
