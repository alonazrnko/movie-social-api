package com.alonazarenko.service;

import com.alonazarenko.dao.dto.FilmRequestDto;
import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.Film;
import com.alonazarenko.model.Genre;
import com.alonazarenko.model.MpaRating;
import com.alonazarenko.storage.film.FilmStorage;
import com.alonazarenko.storage.genre.GenreStorage;
import com.alonazarenko.storage.mpa.MpaStorage;
import com.alonazarenko.storage.user.UserStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Film create(FilmRequestDto dto) {
        log.info("Creating film name={}", dto.getName());

        MpaRating mpa = mpaStorage.getById(dto.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("MPA not found"));

        Set<Genre> genres = dto.getGenres() == null
                ? new HashSet<>()
                : dto.getGenres().stream()
                .map(g -> genreStorage.getById(g.getId())
                        .orElseThrow(() -> new NotFoundException("Genre not found")))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Film film = Film.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(dto.getReleaseDate())
                .duration(dto.getDuration())
                .mpa(mpa)
                .genres(genres)
                .build();

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        log.info("Updating film id={}", film.getId());

        MpaRating mpa = mpaStorage.getById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("MPA not found"));
        film.setMpa(mpa);

        Set<Genre> genres = film.getGenres().stream()
                .map(g -> genreStorage.getById(g.getId())
                        .orElseThrow(() -> new NotFoundException("Genre not found")))
                .collect(Collectors.toSet());
        film.setGenres(genres);


        filmStorage.getById(film.getId())
                .orElseThrow(() -> {
                    log.warn("Film not found: id={}", film.getId());
                    return new NotFoundException("Film with id " + film.getId() + " not found");
                });

        return filmStorage.update(film);
    }

    public Film getById(long id) {
        log.debug("Get film id={}", id);

        return filmStorage.getById(id)
                .orElseThrow(() -> {
                    log.warn("Film not found id={}", id);
                    return new NotFoundException("Film with id " + id + " not found");
                });
    }

    public Collection<Film> getAll() {
        log.debug("Get all films");
        return filmStorage.getAll();
    }

    public void addLike(long filmId, long userId) {
        log.info("Add like filmId={} userId={}", filmId, userId);

        getById(filmId);
        userStorage.getById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", userId);
                    return new NotFoundException("User with id " + userId + " not found");
                });

        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        log.info("Removing like: filmId={}, userId={}", filmId, userId);

        getById(filmId);
        userStorage.getById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", userId);
                    return new NotFoundException("User with id " + userId + " not found");
                });

        filmStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        log.debug("Get top {} films", count);

        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(
                        (Film f) -> f.getLikes().size()
                ).reversed())
                .limit(count)
                .toList();
    }
}