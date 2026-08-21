package com.alonazarenko.dao.dto.film;

import com.alonazarenko.dao.dto.mpa.MpaDto;
import com.alonazarenko.model.Director;
import com.alonazarenko.model.Genre;
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
    private MpaDto mpa;

    private Set<Genre> genres = new HashSet<>();
    private Set<Director> directors = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Long> likes = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate creationDate;
}
