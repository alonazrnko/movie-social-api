package com.alonazarenko.dao.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDate;

@Data
public class NewUserRequest {

    @Email(message = "Incorrect email format")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Login cannot be empty")
    @Pattern(regexp = "\\S+", message = "The login cannot contain spaces")
    private String login;

    private String name;

    @Past(message = "The date of birth cannot be in the future")
    private LocalDate birthday;
}
