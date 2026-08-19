package com.alonazarenko.dao.repository;

import com.alonazarenko.model.Genre;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String FIND_ALL_SQL = "SELECT * FROM genres ORDER BY genre_id";
    private static final String FIND_ALL_BY_ID_SQL = "SELECT g.genre_id, g.name FROM genres g JOIN film_genres fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";
    private static final String FIND_ALL_BY_FILM_ID_SQL = "SELECT genre_id FROM film_genres WHERE film_id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM genres WHERE genre_id = ?";
    private static final String DELETE_GENRES_BY_FILM_SQL = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String INSERT_FILM_GENRE_SQL = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String EXISTS_BY_ID = "SELECT EXISTS(SELECT 1 FROM genres WHERE genre_id = ?)";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_SQL);
    }

    public Optional<Genre> findById(Long id) {
        return findOne(FIND_BY_ID_SQL, id);
    }

    private void updateGenres(long filmId, Set<Long> genreIds) {
        deleteGenresByFilm(filmId);

        if (genreIds == null || genreIds.isEmpty()) {
            return;
        }

        List<Object[]> batchArgs = new ArrayList<>();
        for (Long genreId : genreIds) {
            batchArgs.add(new Object[]{filmId, genreId});
        }

        jdbc.batchUpdate(INSERT_FILM_GENRE_SQL, batchArgs);
    }

    private void deleteGenresByFilm(long filmId) {
        jdbc.update(DELETE_GENRES_BY_FILM_SQL, filmId);
    }

    public List<Genre> findsGenresByFilm(long filmId) {
        return findMany(FIND_ALL_BY_ID_SQL, filmId);
    }

    public Set<Long> findIdsByFilm(long filmId) {
        List<Long> result = jdbc.queryForList(FIND_ALL_BY_FILM_ID_SQL, Long.class, filmId);
        return new HashSet<>(result);
    }

    public void saveGenresIdsByFilm(long filmId, Set<Long> genreIds) {
        deleteGenresByFilm(filmId);
        if (genreIds != null && !genreIds.isEmpty()) {
            updateGenres(filmId, genreIds);
        }
    }

    public boolean existsById(long id) {
        return exists(EXISTS_BY_ID, id);
    }
}
