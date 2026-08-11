package com.alonazarenko.storage.film;

import com.alonazarenko.model.Film;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;

    @Override
    public Film create(Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        log.debug("Film created: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        log.debug("Film updated: {}", film);
        return film;
    }

    @Override
    public Optional<Film> getById(long id) {
        log.debug("Searching film by id={}", id);
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> getAll() {
        log.debug("Getting all films, count={}", films.size());
        return films.values();
    }
}