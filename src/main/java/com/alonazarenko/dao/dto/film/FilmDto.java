package com.alonazarenko.dao.dto.film;

import com.alonazarenko.model.Genre;
import com.alonazarenko.model.MpaRating;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private MpaRating mpa;

    private Set<Genre> genres = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Long> likes = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate creationDate;
}
