package com.alonazarenko.dao.dto.genre;

import com.alonazarenko.model.Genre;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenreMapper {

    public static Genre mapToGenre(GenreDto request) {
        Genre genre = new Genre();
        genre.setName(request.getName());
        return genre;
    }

    public static GenreDto mapToGenreDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }
}
