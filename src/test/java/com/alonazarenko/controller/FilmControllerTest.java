package com.alonazarenko.controller;

import com.alonazarenko.model.Film;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.*;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void initValidation() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    private Film baseFilm() {
        Film f = new Film();
        f.setName("Test film");
        f.setDescription("Desc");
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        f.setDuration(100);
        return f;
    }

    @Test
    void shouldAcceptValidFilm() {
        Film film = baseFilm();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectEmptyName() {
        Film film = baseFilm();
        film.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectNullName() {
        Film film = baseFilm();
        film.setName(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectTooLongDescription() {
        Film film = baseFilm();
        film.setDescription("A".repeat(201));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectReleaseDateBeforeCinemaBirth() {
        Film film = baseFilm();
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }


    @Test
    void shouldRejectNonPositiveDuration() {
        Film film = baseFilm();
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        film.setDuration(-5);
        violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }
}
