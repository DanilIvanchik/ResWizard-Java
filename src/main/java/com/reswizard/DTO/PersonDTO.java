package com.reswizard.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PersonDTO {

    @NotNull(message = "Enter your name.")
    private String username;

    @Min(value = 18, message = "Age must be between 18 and 150 years old.")
    @Max(value = 150, message = "Age must be between 18 and 150 years old.")
    private int age;

    @Email(message = "It is not an email. Enter the valid email.")
    private String email;

    @Size(min = 8, max = 100, message = "Password out of range. Password size should be between 8 and 100 characters.")
    private String password;
}
