package com.alonazarenko.dao.repository;

import com.alonazarenko.model.Film;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?," +
            "duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String FIND_POPULAR_FILMS_WITH_FILTERS_SQL = "SELECT f.* FROM films f LEFT JOIN likes l " +
            "ON f.film_id = l.film_id LEFT JOIN film_genres fg ON f.film_id = fg.film_id WHERE (? IS NULL OR fg.genre_id = ?) " +
            "AND (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?) GROUP BY f.film_id ORDER BY COUNT(l.user_id) " +
            "DESC, f.film_id FETCH FIRST ? ROWS ONLY";
    private static final String FIND_ALL_LIKED_FILMS = "SELECT f.* FROM films f JOIN likes l ON f.film_id = l.film_id " +
            "WHERE l.user_id = ?";
    private static final String DELETE_FILM_SQL = "DELETE FROM films WHERE film_id = ?";


    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Film create(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa()
        );
        film.setId(id);
        return film;
    }

    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa(),
                film.getId()
        );
        return film;
    }

    public boolean delete(long id) {
        return delete(DELETE_FILM_SQL, id);
    }

    public Collection<Film> getAll() {
        return findMany(FIND_ALL_QUERY);
    }


    public Optional<Film> getById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Film> getPopularFilms(Integer genreId, Integer year, int count) {
        return findMany(
                FIND_POPULAR_FILMS_WITH_FILTERS_SQL,
                genreId, genreId,
                year, year,
                count
        );
    }

    public Collection<Film> getLikedFilmsByUserId(long userId) {
        return findMany(FIND_ALL_LIKED_FILMS, userId);
    }
}