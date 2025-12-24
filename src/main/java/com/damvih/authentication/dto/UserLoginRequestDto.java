package com.damvih.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(

        @NotBlank(message = "Username is blank.")
        @Size(min = 5, max = 20, message = "Username should contain between 5 and 20 characters.")
        @Pattern(regexp = "^[a-zA-Z0-9]+[a-zA-Z_0-9]*[a-zA-Z0-9]+$", message = "Username should contain only english letters or digits.")
        String username,

        @NotBlank(message = "Password is blank.")
        @Size(min = 5, max = 20, message = "Username should contain between 5 and 20 characters.")
        @Pattern(regexp = "^[a-zA-Z0-9]+[a-zA-Z_0-9]*[a-zA-Z0-9]+$", message = "Password should contain only english letters or digits.")
        String password

) {
}
