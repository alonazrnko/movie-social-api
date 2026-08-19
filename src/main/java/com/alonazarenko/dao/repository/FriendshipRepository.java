package com.alonazarenko.dao.repository;

import com.alonazarenko.model.Friendship;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FriendshipRepository extends BaseRepository<Friendship> {
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM friendships WHERE user_id = ?";
    private static final String INSERT_SQL = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_BY_IDS_SQL = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";

    public FriendshipRepository(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper);
    }

    public Friendship add(Friendship friendship) {
        jdbc.update(INSERT_SQL, friendship.getUserId(), friendship.getFriendId());

        return friendship;
    }

    public void delete(Long userId, Long friendId) {
        delete(DELETE_BY_IDS_SQL, userId, friendId);
    }

    public List<Friendship> findAllByUserId(Long userId) {
        return findMany(FIND_BY_USER_ID_SQL, userId);
    }
}
