package com.alonazarenko.model;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
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
