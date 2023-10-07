package com.reswizard.Services;

import com.reswizard.Models.User;
import com.reswizard.Repositories.UserRepo;
import com.reswizard.Util.IncorrectAvatarFormatException;
import com.reswizard.Util.MessageLengthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailSender mailSender;
    private static final Logger logger = Logger.getGlobal();

    @Autowired
    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, MailSender mailSender) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public boolean isUsernamePresent(String name) {
        return userRepo.findByUsername(name).isPresent();
    }

    public boolean isUserPresentByEmail(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    public User findUserByUsername(String name) {
        logger.log(Level.INFO, "Finding user by username: " + name);
        return userRepo.findByUsername(name).orElse(null);
    }

    @Transactional
    public void handleAvatarFileUpload(MultipartFile file, String uploadPath) throws IOException {
        logger.log(Level.INFO, "Uploading avatar file: " + file.getOriginalFilename());

        if (file == null || file.isEmpty()) {
            return;
        }

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String uniqueFileName = generateUniqueKey(file.getOriginalFilename());
        if (!isValidAvatarFormat(uniqueFileName)) {
            throw new IncorrectAvatarFormatException("Invalid file format. Only PNG is allowed.");
        }

        String filePath = uploadPath + uniqueFileName;
        file.transferTo(new File(filePath));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> optionalUser = userRepo.findByUsername(username);

        optionalUser.ifPresent(user -> {
            if (!user.getAvatarTitle().equals("defaultAvatar.png")) {
                deleteOldAvatar(uploadPath, user.getAvatarTitle());
            }
            user.setAvatarTitle(uniqueFileName);
            userRepo.save(user);
        });

        logger.log(Level.INFO, "Avatar file upload completed successfully: " + file.getOriginalFilename());
    }

    @Transactional
    public void sendRecoveringEmail(String email) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to ResWizard! Please, visit the following link to recover your password: \nhttp://localhost:8080/user/reset/%s",
                    user.getUsername(),
                    user.getId()
            );

            mailSender.sendMail(user.getEmail(), "Password recovering", message);
            user.setIsInRecovering(true);
            userRepo.save(user);
        }
    }

    private void deleteOldAvatar(String uploadPath, String fileName) {
        File file = new File(uploadPath + fileName);
        if (file.delete()) {
            logger.log(Level.INFO, "Deleted old avatar: " + uploadPath + fileName);
        } else {
            logger.log(Level.INFO, "Old avatar file not found: " + uploadPath + fileName);
        }
    }

    private boolean isValidAvatarFormat(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return "png".equals(fileExtension);
    }

    private String generateUniqueKey(String originalFileName) {
        String uidFile = UUID.randomUUID().toString();
        int lastDotIndex = originalFileName.lastIndexOf(".");
        String extension = (lastDotIndex >= 0) ? originalFileName.substring(lastDotIndex) : "";
        return uidFile + extension;
    }

    public boolean isPasswordValid(String password) {
        return userRepo.findByPassword(passwordEncoder.encode(password)).isPresent();
    }

    public User findUserById(int id) {
        logger.log(Level.INFO, "Finding user by ID: " + id);
        return userRepo.findById(id).orElse(null);
    }

    @Transactional
    public void save(User user) {
        userRepo.save(user);
        logger.log(Level.INFO, "Saved user: " + user.getUsername());
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        logger.log(Level.INFO, "Getting current authenticated user: " + username);
        return userRepo.findByUsername(username).orElse(null);
    }

    /**
     * Checks if the length of the given message is valid.
     *
     * @param message The string representing the message to be checked.
     */
    public void isMessageLengthValid(String message) {
        if (message.length() > 500) {
            throw new MessageLengthException("Message length out of range. Message length should be between 0 and 500 characters.");
        }
    }

    public Optional<User> findByResumePassKey(String key) {
        return userRepo.findByResumePassKey(key);
    }

    @Transactional
    public boolean isActiveUser(String code) {
        Optional<User> optionalUser = userRepo.findByActivationCode(code);
        if (optionalUser.isPresent()) {
            optionalUser.get().setActivationCode(null);
            userRepo.save(optionalUser.get());
            return true;
        }
        return false;
    }

    @Transactional
    public void resetPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user.setIsInRecovering(false);
        save(user);
    }
}
