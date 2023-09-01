package com.reswizard.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "Person")
@Data
public class Person {

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

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Resume> resumes;

}
