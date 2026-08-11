package com.alonazarenko.controller;

import com.alonazarenko.dto.FilmRequestDto;
import com.alonazarenko.model.Film;
import com.alonazarenko.service.FilmService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film create(@RequestBody @Valid FilmRequestDto dto) { return filmService.create(dto); }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) { return filmService.update(film); }

    @GetMapping
    public Collection<Film> getAll() { return filmService.getAll(); }

    @GetMapping("/{id}")
    public Film getById(@PathVariable long id) { return filmService.getById(id); }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) { filmService.addLike(id, userId); }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) { filmService.removeLike(id, userId); }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(defaultValue = "10") int count) { return filmService.getPopularFilms(count); }
}