package com.alonazarenko.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class Film {
    private int id;

    @NotBlank(message = "Film name cannot be emty")
    private String name;

    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;

    @Positive(message = "Duration must be a positive number")
    private int duration;
}
