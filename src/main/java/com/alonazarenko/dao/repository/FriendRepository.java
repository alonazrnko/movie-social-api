package com.alonazarenko.dao.repository;

import com.alonazarenko.model.Friendship;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FriendRepository extends BaseRepository<Friendship> {
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM friends WHERE user_id = ?";
    private static final String INSERT_SQL = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
    private static final String DELETE_BY_IDS_SQL = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String EXISTS_SQL = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ?";

    public FriendRepository(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper);
    }

    public void save(long userId, long friendId) {
        if (exists(userId, friendId)) {
            return;
        }

        jdbc.update(INSERT_SQL, userId, friendId);
    }

    public boolean delete(Long userId, Long friendId) {
        return delete(DELETE_BY_IDS_SQL, userId, friendId);
    }

    public List<Friendship> findAllByUserId(Long userId) {
        return findMany(FIND_BY_USER_ID_SQL, userId);
    }

    public boolean exists(Long userId, Long friendId) {
        Integer count = jdbc.queryForObject(EXISTS_SQL, Integer.class, userId, friendId);
        return count != null && count > 0;
    }
}
