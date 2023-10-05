package com.sharov.insta.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value
public class LoginDto {

    @NotEmpty(message = "Username cannot be empty")
    String username;

    @NotEmpty(message = "Password cannot be empty")
    String password;
}
