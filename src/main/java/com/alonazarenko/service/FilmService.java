package com.alonazarenko.service;

import com.alonazarenko.dao.dto.film.FilmDto;
import com.alonazarenko.dao.dto.film.FilmMapper;
import com.alonazarenko.dao.dto.film.NewFilmRequest;
import com.alonazarenko.dao.dto.film.UpdateFilmRequest;
import com.alonazarenko.dao.repository.FilmRepository;
import com.alonazarenko.dao.repository.UserRepository;
import com.alonazarenko.exception.InternalServerException;
import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.exception.ValidationException;
import com.alonazarenko.model.Film;
import com.alonazarenko.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final FilmMapper filmMapper;
    private final GenreService genreService;
    private final LikeService likeService;
    private final MpaService mpaService;
    private final UserService userService;

    public FilmDto create(NewFilmRequest request) {
        log.info("Creating film name={}", request.getName());

        mpaService.validateMpaExists(request.getMpa());
        genreService.validateGenresExist(request.getGenres());

        Film film = filmMapper.mapToFilm(request);
        film = filmRepository.create(film);

        Set<Long> genres = request.getGenres();
        film.setGenres(genres);
        genreService.saveByFilm(film.getId(), genres);

        return filmMapper.mapToFilmDto(film);
    }

    public FilmDto update(UpdateFilmRequest request) {
        log.info("Updating film id={}", request.getId());

        Film existingFilm = filmRepository.getById(request.getId())
                .orElseThrow(() ->
                        new NotFoundException("Film with id " + request.getId() + " not found")
                );

        mpaService.validateMpaExists(request.getMpa());
        genreService.validateGenresExist(request.getGenres());

        Film updatedFilm = filmMapper.updateFilmFields(existingFilm, request);
        updatedFilm = filmRepository.update(updatedFilm);

        Set<Long> genres = request.getGenres();
        updatedFilm.setGenres(genres);
        genreService.saveByFilm(updatedFilm.getId(), genres);

        updatedFilm.setLikes(likeService.getLikesIdsByFilm(request.getId()));

        return filmMapper.mapToFilmDto(updatedFilm);
    }

    public void delete(long id) {
        getById(id);
        boolean deleted = filmRepository.delete(id);
        if (!deleted) {
            throw new InternalServerException("Failed to delete film with id=" + id);
        }
    }

    public FilmDto getById(long id) {
        log.debug("Get film id={}", id);

        Film film = filmRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Film with id=" + id + " not found"));
        return filmMapper.mapToFilmDto(updateCollections(film));
    }

    public Collection<FilmDto> getAll() {
        log.debug("Get all films");
        return filmRepository.getAll().stream()
                .map(this::updateCollections)
                .map(filmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public Collection<FilmDto> getPopularFilms(Integer genreId, Integer year, int count) {
        log.debug("Get top {} films", count);

        return filmRepository.getPopularFilms(genreId, year, count).stream()
                .map(this::updateCollections)
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

    public Map<Long, Collection<Film>> getLikedFilmsByAllUsers() {
        List<Long> allUsersIds = userRepository.getAll().stream()
                .map(User::getId)
                .toList();

        Map<Long, Collection<Film>> likedFilmsByAllUsers = new HashMap<>();
        for (Long id : allUsersIds) {
            likedFilmsByAllUsers.put(id, filmRepository.getLikedFilmsByUserId(id));
        }

        return likedFilmsByAllUsers;

    }

    public Collection<FilmDto> getRecommendations(long userId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Collection<Film> userLikedFilms = filmRepository.getLikedFilmsByUserId(userId);
        if (userLikedFilms.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Collection<Film>> likedFilmsByAllUsers = getLikedFilmsByAllUsers();
        Set<Film> userLikesSet = new HashSet<>(userLikedFilms);

        Optional<Map.Entry<Long, Collection<Film>>> targetUserEntry = likedFilmsByAllUsers.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(userId))
                .max(Comparator.comparingInt(entry -> {
                    Set<Film> otherLikesSet = new HashSet<>(entry.getValue());
                    Set<Film> intesection = new HashSet<>(userLikesSet);
                    intesection.retainAll(otherLikesSet);
                    return intesection.size();
                }));

        if (targetUserEntry.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<Film> targetUserLikedFilms = targetUserEntry.get().getValue();
        Set<Film> targetLikesSet = new HashSet<>(targetUserLikedFilms);

        targetLikesSet.removeAll(userLikedFilms);

        if (targetLikesSet.isEmpty()) {
            return Collections.emptyList();
        }

        return targetLikesSet.stream()
                .map((this::updateCollections))
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

    public List<FilmDto> getCommonFilms(long userId, long friendId) {
        userService.validateUserExists(userId);
        userService.validateUserExists(friendId);

        if (userId == friendId) {
            throw new ValidationException("User IDs must be different");
        }

        List<Film> commonFilms = filmRepository.getCommonFilms(userId, friendId);
        return commonFilms.stream()
                .map((this::updateCollections))
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

    private Film updateCollections(Film film) {
        long id = film.getId();
        film.setGenres(genreService.getGenresIdByFilm(id));
        film.setLikes(likeService.getLikesIdsByFilm(id));
        return film;
    }
}