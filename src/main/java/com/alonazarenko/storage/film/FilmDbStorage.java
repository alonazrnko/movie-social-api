package com.alonazarenko.storage.film;

import com.alonazarenko.dao.repository.mappers.FilmRowMapper;
import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.Film;
import com.alonazarenko.model.Genre;
import com.alonazarenko.model.MpaRating;
import com.alonazarenko.dao.repository.mappers.GenreRowMapper;
import com.alonazarenko.dao.repository.mappers.MpaRowMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;


import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("filmDbStorage")
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final GenreRowMapper genreRowMapper;
    private final MpaRowMapper mpaRowMapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         FilmRowMapper filmRowMapper,
                         GenreRowMapper genreRowMapper, MpaRowMapper mpaRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
        this.genreRowMapper = genreRowMapper;
        this.mpaRowMapper = mpaRowMapper;
    }

    @Override
    public Film create(Film film) {
        String sql = """
            INSERT INTO films (name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;

        Long mpaId = film.getMpa() != null ? film.getMpa().getId() : null;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setInt(4, film.getDuration());
            ps.setObject(5, mpaId);
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        deleteGenres(film.getId());
        updateGenres(film);

        return getById(film.getId()).orElseThrow();
    }

    @Override
    public Film update(Film film) {
        String sql = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
            WHERE id = ?
            """;

        int updated = jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );

        if (updated == 0) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }

        deleteGenres(film.getId());
        updateGenres(film);

        return getById(film.getId()).orElseThrow();
    }

    @Override
    public Optional<Film> getById(long id) {
        List<Film> films =
                jdbcTemplate.query("SELECT * FROM films WHERE id = ?", filmRowMapper, id);

        if (films.isEmpty()) {
            return Optional.empty();
        }

        Film film = films.get(0);
        loadGenres(film);
        loadMpa(film);
        loadLikes(film);

        return Optional.of(film);
    }

    @Override
    public Collection<Film> getAll() {
        List<Film> films =
                jdbcTemplate.query("SELECT * FROM films", filmRowMapper);

        films.forEach(this::loadGenres);
        films.forEach(this::loadMpa);
        films.forEach(this::loadLikes);

        return films;
    }

    @Override
    public void addLike(long filmId, long userId) {
        jdbcTemplate.update(
                "INSERT INTO likes (film_id, user_id) VALUES (?, ?)",
                filmId, userId
        );
    }

    @Override
    public void removeLike(long filmId, long userId) {
        jdbcTemplate.update(
                "DELETE FROM likes WHERE film_id = ? AND user_id = ?",
                filmId, userId
        );
    }

    private void loadLikes(Film film) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";

        Set<Long> likes = new HashSet<>(
                jdbcTemplate.queryForList(sql, Long.class, film.getId())
        );

        film.setLikes(likes);
    }

    private void updateGenres(Film film) {
        jdbcTemplate.update(
                "DELETE FROM film_genres WHERE film_id = ?",
                film.getId()
        );

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                film.getGenres(),
                film.getGenres().size(),
                (ps, genre) -> {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, genre.getId());
                }
        );
    }

    private void deleteGenres(long filmId) {
        jdbcTemplate.update(
                "DELETE FROM film_genres WHERE film_id = ?",
                filmId
        );
    }

    private void loadGenres(Film film) {
        String sql = """
            SELECT g.id, g.name
            FROM genres g
            JOIN film_genres fg ON g.id = fg.genre_id
            WHERE fg.film_id = ?
            """;

        List<Genre> genres = jdbcTemplate.query(sql, genreRowMapper, film.getId());

        film.setGenres(
                genres.stream()
                        .sorted(Comparator.comparingLong(Genre::getId))
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }

    private void loadMpa(Film film) {
        if (film.getMpa() == null) {
            return;
        }

        List<MpaRating> list = jdbcTemplate.query(
                "SELECT id, name FROM mpa_ratings WHERE id = ?",
                mpaRowMapper,
                film.getMpa().getId()
        );

        if (!list.isEmpty()) {
            film.setMpa(list.get(0));
        }
    }
}
