package com.reswizard.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationDTO {

    @NotNull(message = "Enter your name.")
    private String username;

    @Size(min = 8, max = 100, message = "Incorrect password, try again!")
    private String password;

}
