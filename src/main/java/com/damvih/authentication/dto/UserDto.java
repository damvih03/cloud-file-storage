package com.damvih.authentication.dto;

import java.io.Serializable;

public record UserDto(

        Long id,
        String username

) implements Serializable {
}
