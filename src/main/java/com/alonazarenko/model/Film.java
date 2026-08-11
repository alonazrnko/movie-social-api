package com.alonazarenko.model;

import com.alonazarenko.validation.ReleaseDateConstraint;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    @ReleaseDateConstraint
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private Set<Genre> genres = new HashSet<>();

    @NotNull(message = "MPA rating is required")
    private MpaRating mpa;

    private Set<Long> likes = new HashSet<>();
}
