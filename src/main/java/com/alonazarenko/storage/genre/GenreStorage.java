package com.alonazarenko.storage.genre;

import com.alonazarenko.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    List<Genre> getAll();
    Optional<Genre> getById(long id);
}
