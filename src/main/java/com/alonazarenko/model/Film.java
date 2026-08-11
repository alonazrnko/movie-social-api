package com.alonazarenko.model;

import com.alonazarenko.validation.ReleaseDateConstraint;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    private MpaRating mpa;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();
}
