package com.alonazarenko.storage.film;

import com.alonazarenko.model.Film;
import com.alonazarenko.model.MpaRating;
import com.alonazarenko.storage.genre.GenreRowMapper;
import com.alonazarenko.storage.mpa.MpaRowMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        FilmDbStorage.class,
        FilmRowMapper.class,
        GenreRowMapper.class,
        MpaRowMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    private Film savedFilm;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM films");

        Film film = Film.builder()
                .name("Test")
                .description("Desc")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(new MpaRating(1, null))
                .build();

        savedFilm = filmStorage.create(film);
    }

    @Test
    void shouldFindFilmById() {
        Optional<Film> filmOptional = filmStorage.getById(savedFilm.getId());

        assertThat(filmOptional).isPresent();
    }

    @Test
    void shouldUpdateFilm() {
        Film film = filmStorage.getById(1).orElseThrow();
        film.setName("Updated Name");

        filmStorage.update(film);

        Film updated = filmStorage.getById(1).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    void shouldReturnAllFilms() {
        assertThat(filmStorage.getAll())
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    void shouldCreateFilm() {
        Film film = Film.builder()
                .name("Test")
                .description("Desc")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(new MpaRating(1, null))
                .build();

        Film saved = filmStorage.create(film);

        assertThat(saved.getId()).isPositive();
        assertThat(saved.getId()).isNotEqualTo(1);
    }
}
