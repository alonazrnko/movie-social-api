package com.alonazarenko.dao.repository;

import com.alonazarenko.model.Film;
import com.alonazarenko.model.Like;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class LikeRepository extends BaseRepository<Like> {
    private static final String FIND_USER_IDS_BY_FILM_ID_SQL = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String INSERT_SQL = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_BY_IDS_SQL = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    public LikeRepository(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper);
    }

    public void addLike(long filmId, long userId) {
        update(INSERT_SQL, filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        update(DELETE_BY_IDS_SQL, filmId, userId);
    }

    private void loadLikes(Film film) {
        Set<Long> likes = new HashSet<>(
                jdbc.queryForList(FIND_USER_IDS_BY_FILM_ID_SQL, Long.class, film.getId())
        );

        film.setLikes(likes);
    }
}
