package com.reswizard.Services;

import com.reswizard.Models.User;
import com.reswizard.Repositories.UserRepo;
import com.reswizard.Security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public UserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Loads a user by their username.
     *
     * @param username The username of the user to load.
     * @return UserDetails object representing the loaded user.
     * @throws UsernameNotFoundException If the user with the given username is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isEmpty()){
            throw  new UsernameNotFoundException("User name not found!");
        }else {
            return new UserDetailsImpl(user.get());
        }

    }

}
