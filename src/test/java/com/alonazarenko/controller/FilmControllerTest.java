package com.alonazarenko.controller;

import com.alonazarenko.exception.ValidationException;
import com.alonazarenko.model.Film;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController controller;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
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
    void shouldAddValidFilm() {
        Film film = baseFilm();

        Film created = controller.addFilm(film);

        assertNotNull(created);
        assertEquals(1, created.getId());
        assertEquals("Test film", created.getName());
    }

    @Test
    void shouldRejectEmptyName() {
        Film film = baseFilm();
        film.setName("");

        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    void shouldRejectNullName() {
        Film film = baseFilm();
        film.setName(null);

        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    void shouldRejectTooLongDescription() {
        Film film = baseFilm();
        film.setDescription("A".repeat(201));

        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    void shouldRejectReleaseDateBeforeCinemaBirth() {
        Film film = baseFilm();
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    void shouldRejectNegativeDuration() {
        Film film = baseFilm();
        film.setDuration(-5);

        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    void shouldRejectZeroDuration() {
        Film film = baseFilm();
        film.setDuration(0);

        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    void shouldFailOnNullFilm() {
        assertThrows(NullPointerException.class, () -> controller.addFilm(null));
    }
}
