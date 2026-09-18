package com.alonazarenko.repository;

import com.alonazarenko.dao.repository.GenreRepository;
import com.alonazarenko.dao.repository.mappers.GenreRowMapper;
import com.alonazarenko.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
class GenreRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
        GenreRowMapper mapper = new GenreRowMapper();
        genreRepository = new GenreRepository(jdbcTemplate, mapper);

        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM genres");

        jdbcTemplate.update("INSERT INTO genres (genre_id, name) VALUES (1, 'Comedy')");
        jdbcTemplate.update("INSERT INTO genres (genre_id, name) VALUES (2, 'Drama')");
        jdbcTemplate.update("INSERT INTO genres (genre_id, name) VALUES (3, 'Animation')");
        jdbcTemplate.update("INSERT INTO genres (genre_id, name) VALUES (4, 'Thriller')");
        jdbcTemplate.update("INSERT INTO genres (genre_id, name) VALUES (5, 'Documentary')");
        jdbcTemplate.update("INSERT INTO genres (genre_id, name) VALUES (6, 'Action')");

        jdbcTemplate.update("INSERT INTO films (film_id, name, description, release_date, duration, mpa_id) VALUES (1, 'Test Film', 'Description', '2000-01-01', 120, 1)");

        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (1, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (1, 2)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (1, 3)");
    }

    @Test
    void testFindAll() {
        List<Genre> genres = genreRepository.findAll();

        assertThat(genres).hasSize(6);
        assertThat(genres).extracting(Genre::getName)
                .containsExactlyInAnyOrder(
                        "Comedy", "Drama", "Animation",
                        "Thriller", "Documentary", "Action"
                );
    }

    @Test
    void testFindById() {
        Optional<Genre> genre = genreRepository.findById(1L);

        assertThat(genre).isPresent();
        assertThat(genre.get().getId()).isEqualTo(1L);
        assertThat(genre.get().getName()).isEqualTo("Comedy");
    }

    @Test
    void testFindById_WhenNotFound() {
        Optional<Genre> genre = genreRepository.findById(999L);

        assertThat(genre).isEmpty();
    }

    @Test
    void testFindIdsByFilm() {
        Set<Long> genreIds = genreRepository.findIdsByFilm(1L);

        assertThat(genreIds).hasSize(3);
        assertThat(genreIds).containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    void testFindIdsByFilm_WhenNoGenres() {
        jdbcTemplate.update("INSERT INTO films (film_id, name, description, release_date, duration, mpa_id) VALUES (2, 'Film Without Genres', 'Description', '2001-01-01', 130, 1)");

        Set<Long> genreIds = genreRepository.findIdsByFilm(2L);

        assertThat(genreIds).isEmpty();
    }

    @Test
    void testFindsGenresByFilm() {
        List<Genre> genres = genreRepository.findsGenresByFilm(1L);

        assertThat(genres).hasSize(3);
        assertThat(genres).extracting(Genre::getName)
                .containsExactlyInAnyOrder("Comedy", "Drama", "Animation");
    }

    @Test
    void testSaveGenresIdsByFilm() {
        Set<Long> newGenreIds = new HashSet<>();
        newGenreIds.add(4L);
        newGenreIds.add(5L);
        newGenreIds.add(6L);

        genreRepository.saveGenresIdsByFilm(1L, newGenreIds);

        Set<Long> updatedGenreIds = genreRepository.findIdsByFilm(1L);
        assertThat(updatedGenreIds).hasSize(3);
        assertThat(updatedGenreIds).containsExactlyInAnyOrder(4L, 5L, 6L);
    }

    @Test
    void testSaveGenresIdsByFilm_WithEmptySet() {
        Set<Long> emptyGenreIds = new HashSet<>();

        genreRepository.saveGenresIdsByFilm(1L, emptyGenreIds);

        Set<Long> updatedGenreIds = genreRepository.findIdsByFilm(1L);
        assertThat(updatedGenreIds).isEmpty();
    }

    @Test
    void testSaveGenresIdsByFilm_WithNullSet() {
        genreRepository.saveGenresIdsByFilm(1L, null);

        Set<Long> updatedGenreIds = genreRepository.findIdsByFilm(1L);
        assertThat(updatedGenreIds).isEmpty();
    }
}
