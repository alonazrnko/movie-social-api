package com.alonazarenko.dao.repository;

import com.alonazarenko.model.Film;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_id)" +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?," +
            "duration = ?, mpa_id = ? WHERE film_id = ?";

    private static final String DELETE_FILM_SQL = "DELETE FROM films WHERE film_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE film_id = ?";

    private static final String FIND_POPULAR_FILMS_WITH_FILTERS_SQL = "SELECT f.* FROM films f LEFT JOIN likes l " +
            "ON f.film_id = l.film_id LEFT JOIN film_genres fg ON f.film_id = fg.film_id WHERE (? IS NULL OR fg.genre_id = ?) " +
            "AND (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?) GROUP BY f.film_id ORDER BY COUNT(l.user_id) " +
            "DESC, f.film_id FETCH FIRST ? ROWS ONLY";

    private static final String FIND_ALL_LIKED_FILMS = "SELECT f.* FROM films f JOIN likes l ON f.film_id = l.film_id " +
            "WHERE l.user_id = ?";

    private static final String FIND_COMMON_FILMS = "SELECT f.* FROM films f INNER JOIN likes l1 ON f.film_id = l1.film_id " +
            "AND l1.user_id = ? INNER JOIN likes l2 ON f.film_id = l2.film_id AND l2.user_id = ? LEFT JOIN likes l_count " +
            "ON f.film_id = l_count.film_id GROUP BY f.film_id ORDER BY COUNT(l_count.user_id) DESC";

    private static final String FIND_BY_DIRECTOR_SORTED_BY_YEAR_SQL = "SELECT f.film_id, f.name, f.description, f.release_date, " +
            " f.duration, f.mpa_id, m.name AS name FROM films f JOIN film_directors fd ON f.film_id = fd.film_id " +
            "LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id WHERE fd.director_id = ? ORDER BY f.release_date";

    private static final String FIND_BY_DIRECTOR_SORTED_BY_LIKES_SQL = "SELECT f.film_id, f.name, f.description, f.release_date, " +
            "f.duration, f.mpa_id, m.name AS name, COUNT(l.user_id) AS likes_count FROM films f " +
            "JOIN film_directors fd ON f.film_id = fd.film_id LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN likes l ON f.film_id = l.film_id WHERE fd.director_id = ? GROUP BY f.film_id, f.name, f.description, " +
            "f.release_date, f.duration, f.mpa_id, m.name ORDER BY likes_count DESC";



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

    public List<Film> getCommonFilms(long userId, long friendId) {
        return findMany(FIND_COMMON_FILMS, userId, friendId);
    }

    public List<Film> findByDirectorIdSorted(Long directorId, String sortBy) {
        if ("year".equals(sortBy)) {
            return findMany(FIND_BY_DIRECTOR_SORTED_BY_YEAR_SQL, directorId);
        } else if ("likes".equals(sortBy)) {
            return findMany(FIND_BY_DIRECTOR_SORTED_BY_LIKES_SQL, directorId);
        } else {
            return findMany(FIND_BY_DIRECTOR_SORTED_BY_YEAR_SQL, directorId);
        }
    }
}