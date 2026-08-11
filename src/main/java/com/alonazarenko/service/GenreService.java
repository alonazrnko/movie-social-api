package com.alonazarenko.service;

import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.Genre;
import com.alonazarenko.storage.genre.GenreStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> getAllGenres() {
        return genreStorage.getAll();
    }

    public Genre getGenreById(int id) {
        return genreStorage.getById(id)
                .orElseThrow(() ->
                        new NotFoundException("Genre with id=" + id + " not found"));
    }
}
