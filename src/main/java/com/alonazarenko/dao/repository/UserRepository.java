package com.alonazarenko.dao.repository;

import com.alonazarenko.dao.dto.user.UserDto;
import com.alonazarenko.dao.dto.user.UserMapper;
import com.alonazarenko.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class UserRepository extends BaseRepository<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_ALL_FRIENDS =
            "SELECT u.* FROM users AS u " +
                    "JOIN friendships AS f ON u.user_id = f.friend_id " +
                    "WHERE f.user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM users WHERE user_id = ?";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public User create(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    public boolean delete(long id) {
        return delete(DELETE_USER_SQL, id);
    }

    public Collection<User> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> getById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<UserDto> getAllFriends(long userId) {
        return findMany(FIND_ALL_FRIENDS, userId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }
}
