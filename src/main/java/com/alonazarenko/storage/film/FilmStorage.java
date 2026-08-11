package com.alonazarenko.storage.film;

import com.alonazarenko.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Optional<Film> getById(long id);

    Collection<Film> getAll();
}