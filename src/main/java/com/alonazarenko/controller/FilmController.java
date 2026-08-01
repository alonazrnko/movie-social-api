package com.alonazarenko.controller;

import com.alonazarenko.exception.ValidationException;
import com.alonazarenko.model.Film;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final List<Film> films = new ArrayList<>();
    private int nextId = 1;

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private void validateDate(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Release date cannot be earlier than 28.12.1895");
        }
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validate(film);
        validateDate(film);
        film.setId(nextId++);
        films.add(film);
        log.info("New film added: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validate(film);
        validateDate(film);
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == film.getId()) {
                films.set(i, film);
                log.info("Film updated: {}", film);
                return film;
            }
        }

        log.warn("Film update failed. Film with id {} not found", film.getId());
        throw new ValidationException("Фильм с id " + film.getId() + " не найден");
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return films;
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Film name cannot be empty");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Description cannot exceed 200 characters");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Duration must be a positive number");
        }
    }
}