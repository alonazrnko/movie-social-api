package com.alonazarenko.service;

import com.alonazarenko.dao.dto.genre.GenreDto;
import com.alonazarenko.dao.dto.genre.GenreMapper;
import com.alonazarenko.dao.repository.GenreRepository;
import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.Genre;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<GenreDto> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(GenreMapper::mapToGenreDto)
                .toList();
    }

    public GenreDto getGenreById(long genreId) {
        return genreRepository.findById(genreId)
                .map(GenreMapper::mapToGenreDto)
                .orElseThrow(() -> new NotFoundException("Genre with ID: " + genreId + " not found"));
    }

    public void saveByFilm(long id, Set<Long> genres) {
        genreRepository.saveGenresIdsByFilm(id, genres);
    }

    public Set<Long> getGenresIdByFilm(long filmId) {
        return genreRepository.findIdsByFilm(filmId);
    }

    public Set<Genre> getGenresByFilmId(long filmId) {
        return new LinkedHashSet<>(genreRepository.findsGenresByFilm(filmId));
    }

    public void validateGenresExist(Set<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return;
        }

        for (Long id : genreIds) {
            if (!genreRepository.existsById(id)) {
                throw new NotFoundException("Genre with id " + id + " not found");
            }
        }
    }
}
