package com.alonazarenko.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private long id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "\\S+", message = "Login cannot contain spaces")
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;
}
