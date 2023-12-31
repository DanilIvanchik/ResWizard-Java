package com.reswizard.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @NotNull(message = "Enter your name.")
    @Column(name = "username")
    private String username;

    @Min(value = 18, message = "Age must be between 18 and 150 years old.")
    @Max(value = 150, message = "Age must be between 18 and 150 years old.")
    @Column(name = "age")
    private int age;

    @Email(message = "It is not an email. Enter your Email.")
    @NotNull(message = "This field should not be empty. Enter your Email.")
    private String email;

    @Size(min = 8, max = 100, message = "Password out of range. Password size should be between 8 and 100 characters.")
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "message")
    @Size(max = 500, message = "Message length out of range. Message length should be between 0 and 500 characters.")
    private String message;

    @Column(name = "avatar_title")
    private String avatarTitle;

    @Column(name = "is_in_recovering")
    private Boolean isInRecovering;

    @Column(name = "resume_pass_key")
    private String resumePassKey;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Resume> resumes;

}
