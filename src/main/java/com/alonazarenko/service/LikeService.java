package com.alonazarenko.service;

import com.alonazarenko.dao.dto.like.LikeMapper;
import com.alonazarenko.dao.repository.FilmRepository;
import com.alonazarenko.dao.repository.LikeRepository;
import com.alonazarenko.dao.repository.UserRepository;
import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.Like;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final FilmRepository filmRepository;


    public void addLike(long filmId, long userId) {
        log.info("Add like filmId={} userId={}", filmId, userId);

        Set<Long> userLikes = likeRepository.findUserIdsByFilmId(filmId);
        if (userLikes.contains(userId)) {
            Like like = new Like(filmId, userId);
            LikeMapper.mapToLikeDto(like);
        }

        Like like = new Like(filmId, userId);
        likeRepository.addLike(like);

        LikeMapper.mapToLikeDto(like);
    }

    public void removeLike(long filmId, long userId) {
        log.info("Removing like: filmId={}, userId={}", filmId, userId);

        filmRepository.getById(filmId);
        userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", userId);
                    return new NotFoundException("User with id " + userId + " not found");
                });

        likeRepository.removeLike(filmId, userId);
    }

    public Set<Long> getLikesIdsByFilm(long filmId) {
        return likeRepository.findUserIdsByFilmId(filmId);
    }
}
