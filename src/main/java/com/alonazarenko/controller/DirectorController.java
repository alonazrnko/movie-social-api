package com.alonazarenko.controller;

import com.alonazarenko.dao.dto.director.DirectorDto;
import com.alonazarenko.dao.dto.director.NewDirectorRequest;
import com.alonazarenko.service.DirectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<DirectorDto> getAll() {
        log.info("Director: request to get all directors");
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public DirectorDto getById(@PathVariable Long id) {
        return directorService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto create(@Valid @RequestBody NewDirectorRequest director) {
        return directorService.create(director);
    }

    @PutMapping
    public DirectorDto update(@Valid @RequestBody DirectorDto director) {
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        directorService.delete(id);
    }
}
