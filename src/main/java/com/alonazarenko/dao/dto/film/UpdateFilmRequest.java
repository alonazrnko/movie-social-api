package com.alonazarenko.dao.dto.film;

import com.alonazarenko.model.MpaRating;
import com.alonazarenko.validation.ReleaseDateConstraint;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UpdateFilmRequest {

    @NotNull(message = "ID is required")
    @Positive(message = "ID must be positive")
    private Long id;

    private String name;

    @Size(max = 200, message = "The description cannot exceed 200 characters")
    private String description;

    @ReleaseDateConstraint
    private LocalDate releaseDate;

    private Integer duration;
    private Long mpa;

    private Set<Long> genres = new HashSet<>();
    private Set<Long> directors = new HashSet<>();

    @JsonSetter("genres")
    public void setGenresFromMaps(Set<Map<String, Long>> genreMaps) {
        if (genreMaps != null) {
            this.genres = genreMaps.stream()
                    .map(map -> map.get("id"))
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());
        }
    }

    @JsonSetter("mpa")
    public void setMpaToLong(MpaRating mpa) {
        if (mpa != null) {
            this.mpa = mpa.getId();
        }
    }

    @JsonSetter("directors")
    public void setDirectorsFromMaps(Set<Map<String, Long>> directorMaps) {
        if (directorMaps != null) {
            this.directors = directorMaps.stream()
                    .map(map -> map.get("id"))
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());
        }
    }

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null;
    }

    public boolean hasMpa() {
        return mpa != null;
    }

    public boolean hasGenres() {
        return genres != null && !genres.isEmpty();
    }
}