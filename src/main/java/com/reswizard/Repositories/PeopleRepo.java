package com.reswizard.Repositories;

import com.reswizard.Models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeopleRepo extends JpaRepository<Person, Integer> {
    Optional<Person> findByUsername(String name);

    Optional<Person> findByEmail(String string);

    Optional<Person> findByPassword(String password);
    Optional<Person> findPersonById(int id);

    Optional<Person> findByActivationCode(String code);
}
