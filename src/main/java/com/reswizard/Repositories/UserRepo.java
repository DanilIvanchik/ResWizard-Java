package com.reswizard.Repositories;

import com.reswizard.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String name);

    Optional<User> findByEmail(String string);

    Optional<User> findByPassword(String password);
    Optional<User> findById(int id);

    Optional<User> findByActivationCode(String code);

    Optional<User> findByResumePassKey(String key);
}
