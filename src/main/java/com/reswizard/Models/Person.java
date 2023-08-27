package com.reswizard.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

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

    @Email
    @NotNull(message = "Enter your Email.")
    private String email;

    @Size(min = 8, max = 100, message = "Password out of range. Password size should be between 8 and 100 characters.")
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

}
