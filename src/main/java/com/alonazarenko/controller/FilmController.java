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

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        film.setId(nextId++);
        films.add(film);
        log.info("New film added: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == film.getId()) {
                films.set(i, film);
                log.info("Film updated: {}", film);
                return film;
            }
        }

        log.warn("Film update failed. Film with id {} not found", film.getId());
        throw new ValidationException("Film with id " + film.getId() + " not found");
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return films;
    }
}