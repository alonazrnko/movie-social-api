package com.alonazarenko.service;

import com.alonazarenko.dao.dto.director.DirectorDto;
import com.alonazarenko.dao.dto.director.DirectorMapper;
import com.alonazarenko.dao.dto.director.NewDirectorRequest;
import com.alonazarenko.dao.repository.DirectorRepository;
import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.Director;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorMapper directorMapper;

    private final DirectorRepository directorRepository;

    public DirectorDto create(NewDirectorRequest director) {
        Director directorNew = directorRepository.create(director);
        return directorMapper.mapToDirectorDto(directorNew);
    }

    public DirectorDto update(DirectorDto director) {
        if (directorRepository.findById(director.getId()).isEmpty()) {
            throw new NotFoundException("Director with id " + director.getId() + "not found");
        }
        Director directorUpdate = directorRepository.update(director);
        return directorMapper.mapToDirectorDto(directorUpdate);
    }

    public void delete(Long id) {
        directorRepository.delete(id);
    }

    public Collection<DirectorDto> getAll() {
        return directorRepository.findAll().stream()
                .map(directorMapper::mapToDirectorDto)
                .toList();
    }

    public DirectorDto getById(Long id) {
        return directorRepository.findById(id)
                .map(directorMapper::mapToDirectorDto)
                .orElseThrow(() -> new NotFoundException("Director with id" + id + " not found"));
    }

    public Set<Long> getDirectorsIdsByFilm(long id) {
        List<Director> directors = directorRepository.findDirectorsByFilmId(id);
        Set<Long> result = new HashSet<>();
        for (Director d : directors) {
            result.add(d.getId());
        }
        return result;
    }

    public void validateDirectorExists(Set<Long> directorIds) {
        if (directorIds == null || directorIds.isEmpty()) {
            return;
        }

        for (Long id : directorIds) {
            if (!directorRepository.existsById(id)) {
                throw new NotFoundException("Director with id " + id + " not found");
            }
        }
    }
}
