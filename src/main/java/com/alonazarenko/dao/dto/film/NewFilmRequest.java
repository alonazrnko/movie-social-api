package com.alonazarenko.dao.dto.film;

import com.alonazarenko.model.MpaRating;
import com.alonazarenko.validation.ReleaseDateConstraint;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class NewFilmRequest {
    @NotBlank(message = "The name cannot be empty")
    private String name;

    @Size(max = 200, message = "The description cannot exceed 200 characters")
    private String description;

    @NotNull(message = "Release date is required")
    @PastOrPresent(message = "The release date cannot be in the future")
    @ReleaseDateConstraint
    private LocalDate releaseDate;

    @Positive(message = "The duration must be positive")
    private Integer duration;

    @NotNull(message = "MPA Rating is required")
    private Long mpa;

    private Set<Long> genres = new HashSet<>();

    @JsonSetter("genres")
    public void setGenresFromMaps(Set<Map<String, Long>> genreMaps) {
        if (genreMaps != null) {
            this.genres = genreMaps.stream().map(map -> map.get("id"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }
    }

    @JsonSetter("mpa")
    public void setMpaToLong(MpaRating mpa) {
        if (mpa != null) {
            this.mpa = mpa.getId();
        }
    }
}
