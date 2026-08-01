package com.alonazarenko.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class User {

    private int id;

    @NotNull
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "\\S+", message = "Login cannot contain spaces")
    private String login;

    private String name;

    @PastOrPresent(message = "Birthday cannot be in the future")
    private LocalDate birthday;
}
