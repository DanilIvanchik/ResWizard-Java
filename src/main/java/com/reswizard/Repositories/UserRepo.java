package com.reswizard.Repositories;

import com.reswizard.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    /**
     * Retrieve a user by their username.
     *
     * @param name The username to search for.
     * @return An optional containing the user with the specified username, or empty if not found.
     */
    Optional<User> findByUsername(String name);

    /**
     * Retrieve a user by their email address.
     *
     * @param email The email address to search for.
     * @return An optional containing the user with the specified email, or empty if not found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Retrieve a user by their encoded password.
     *
     * @param password The encoded password to search for.
     * @return An optional containing the user with the specified encoded password, or empty if not found.
     */
    Optional<User> findByPassword(String password);

    /**
     * Retrieve a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return An optional containing the user with the specified ID, or empty if not found.
     */
    Optional<User> findById(int id);

    /**
     * Retrieve a user by their activation code.
     *
     * @param code The activation code to search for.
     * @return An optional containing the user with the specified activation code, or empty if not found.
     */
    Optional<User> findByActivationCode(String code);

    /**
     * Retrieve a user by their resume pass key.
     *
     * @param key The resume pass key to search for.
     * @return An optional containing the user with the specified resume pass key, or empty if not found.
     */
    Optional<User> findByResumePassKey(String key);
}
