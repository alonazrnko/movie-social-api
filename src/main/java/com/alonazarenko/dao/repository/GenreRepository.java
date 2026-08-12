package com.alonazarenko.dao.repository;

import com.alonazarenko.model.Film;
import com.alonazarenko.model.Genre;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String FIND_ALL_SQL = "SELECT * FROM genre ORDER BY genre_id";
    private static final String FIND_ALL_BY_ID_SQL = "SELECT g.id, g.name FROM genres g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM genre WHERE genre_id = ?";
    private static final String DELETE_GENRES_BY_FILM_SQL = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String INSERT_FILM_GENRE_SQL = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_SQL);
    }

    public Optional<Genre> findById(Long id) {
        return findOne(FIND_BY_ID_SQL, id);
    }

    private void updateGenres(Film film) {
        deleteGenresByFilm(film.getId());

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        jdbc.batchUpdate(
                INSERT_FILM_GENRE_SQL,
                film.getGenres(),
                film.getGenres().size(),
                (ps, genre) -> {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, genre.getId());
                }
        );
    }

    private void deleteGenresByFilm(long filmId) {
        jdbc.update(DELETE_GENRES_BY_FILM_SQL, filmId);
    }

    private void loadGenres(Film film) {
        List<Genre> genres = jdbc.query(FIND_ALL_BY_ID_SQL, mapper, film.getId());

        film.setGenres(
                genres.stream()
                        .sorted(Comparator.comparingLong(Genre::getId))
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }
}
