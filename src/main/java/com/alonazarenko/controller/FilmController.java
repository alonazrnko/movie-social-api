package com.alonazarenko.controller;

import com.alonazarenko.dao.dto.film.FilmDto;
import com.alonazarenko.dao.dto.film.NewFilmRequest;
import com.alonazarenko.dao.dto.film.UpdateFilmRequest;
import com.alonazarenko.service.FilmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public FilmDto create(@RequestBody @Valid NewFilmRequest request) {
        return filmService.create(request);
    }

    @PutMapping
    public FilmDto update(@RequestBody @Valid UpdateFilmRequest request) {
        return filmService.update(request);
    }

    @GetMapping
    public Collection<FilmDto> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public FilmDto getById(@PathVariable long id) {
        return filmService.getById(id);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getPopularFilms(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year
    ) {
        return filmService.getPopularFilms(genreId, year, count);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable long id) {
        filmService.delete(id);
    }
}