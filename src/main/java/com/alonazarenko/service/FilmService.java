package com.alonazarenko.service;

import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.Film;
import com.alonazarenko.storage.film.FilmStorage;
import com.alonazarenko.storage.user.UserStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        log.info("Creating film: name='{}'", film.getName());
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        log.info("Updating film id={}", film.getId());

        filmStorage.getById(film.getId())
                .orElseThrow(() -> {
                    log.warn("Film not found: id={}", film.getId());
                    return new NotFoundException("Film with id " + film.getId() + " not found");
                });

        return filmStorage.update(film);
    }

    public Film getById(long id) {
        log.info("Getting film by id={}", id);

        return filmStorage.getById(id)
                .orElseThrow(() -> {
                    log.warn("Film not found: id={}", id);
                    return new NotFoundException("Film with id " + id + " not found");
                });
    }

    public Collection<Film> getAll() {
        log.info("Getting all films");
        return filmStorage.getAll();
    }

    public void addLike(long filmId, long userId) {
        log.info("Adding like: filmId={}, userId={}", filmId, userId);

        Film film = getById(filmId);

        userStorage.getById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", userId);
                    return new NotFoundException("User with id " + userId + " not found");
                });

        film.getLikes().add(userId);
    }

    public void removeLike(long filmId, long userId) {
        log.info("Removing like: filmId={}, userId={}", filmId, userId);

        Film film = getById(filmId);

        userStorage.getById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", userId);
                    return new NotFoundException("User with id " + userId + " not found");
                });

        film.getLikes().remove(userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        log.info("Getting top {} popular films", count);

        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(
                        (Film f) -> f.getLikes().size()
                ).reversed())
                .limit(count)
                .toList();
    }
}