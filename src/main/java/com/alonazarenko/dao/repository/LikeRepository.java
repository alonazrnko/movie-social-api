package com.alonazarenko.dao.repository;

import com.alonazarenko.model.Like;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class LikeRepository extends BaseRepository<Like> {
    private static final String FIND_USER_IDS_BY_FILM_ID_SQL = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String INSERT_SQL = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_BY_IDS_SQL = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    public LikeRepository(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper);
    }

    public void addLike(Like like) {
        update(INSERT_SQL, like.getFilmId(), like.getUserId());
    }

    public void removeLike(Long filmId, Long userId) {
        update(DELETE_BY_IDS_SQL, filmId, userId);
    }

    public Set<Long> findUserIdsByFilmId(Long filmId) {
        List<Long> userIds = jdbc.queryForList(FIND_USER_IDS_BY_FILM_ID_SQL, Long.class, filmId);
        return new HashSet<>(userIds);
    }
}
