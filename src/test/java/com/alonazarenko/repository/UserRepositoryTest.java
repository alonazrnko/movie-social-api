package com.alonazarenko.repository;

import com.alonazarenko.dao.repository.UserRepository;
import com.alonazarenko.dao.repository.mappers.UserRowMapper;
import com.alonazarenko.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
class UserRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        UserRowMapper mapper = new UserRowMapper();
        userRepository = new UserRepository(jdbcTemplate, mapper);

        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM friendships");

        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES ('user1@test.com', 'user1', 'User One', '1990-01-01')");
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES ('user2@test.com', 'user2', 'User Two', '1990-02-02')");
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES ('user3@test.com', 'user3', 'User Three', '1990-03-03')");

        Long userId1 = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'user1@test.com'", Long.class);
        Long userId2 = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'user2@test.com'", Long.class);
        Long userId3 = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'user3@test.com'", Long.class);

        jdbcTemplate.update("INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)", userId1, userId2);
        jdbcTemplate.update("INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)", userId1, userId3);
    }

    @Test
    void testCreate() {
        User newUser = new User();
        newUser.setEmail("newuser@test.com");
        newUser.setLogin("newuser");
        newUser.setName("New User");
        newUser.setBirthday(LocalDate.of(1995, 5, 5));

        User createdUser = userRepository.create(newUser);

        assertThat(createdUser.getId()).isPositive();
        assertThat(createdUser.getEmail()).isEqualTo("newuser@test.com");
        assertThat(createdUser.getLogin()).isEqualTo("newuser");

        Optional<User> retrievedUser = userRepository.getById(createdUser.getId());
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getEmail()).isEqualTo("newuser@test.com");
    }

    @Test
    void testUpdate() {
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'user1@test.com'", Long.class);

        User userToUpdate = new User();
        userToUpdate.setId(userId);
        userToUpdate.setEmail("updated@test.com");
        userToUpdate.setLogin("updateduser");
        userToUpdate.setName("Updated User");
        userToUpdate.setBirthday(LocalDate.of(1991, 1, 1));

        User updatedUser = userRepository.update(userToUpdate);

        assertThat(updatedUser.getEmail()).isEqualTo("updated@test.com");
        assertThat(updatedUser.getLogin()).isEqualTo("updateduser");
        assertThat(updatedUser.getName()).isEqualTo("Updated User");

        Optional<User> retrievedUser = userRepository.getById(userId);
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getEmail()).isEqualTo("updated@test.com");
        assertThat(retrievedUser.get().getName()).isEqualTo("Updated User");
    }

    @Test
    void testGetAll() {
        Collection<User> users = userRepository.getAll();

        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder(
                        "user1@test.com",
                        "user2@test.com",
                        "user3@test.com"
                );
    }

    @Test
    void testGetById() {
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'user1@test.com'", Long.class);

        Optional<User> user = userRepository.getById(userId);

        assertThat(user).isPresent();
        assertThat(user.get().getId()).isEqualTo(userId);
        assertThat(user.get().getEmail()).isEqualTo("user1@test.com");
        assertThat(user.get().getLogin()).isEqualTo("user1");
        assertThat(user.get().getName()).isEqualTo("User One");
    }

    @Test
    void testGetById_WhenNotFound() {
        Optional<User> user = userRepository.getById(999L);

        assertThat(user).isEmpty();
    }


    @Test
    void testGetAllFriends() {
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'user1@test.com'", Long.class);

        List<com.alonazarenko.dao.dto.user.UserDto> friends = userRepository.getAllFriends(userId);

        assertThat(friends).hasSize(2);
        assertThat(friends).extracting(com.alonazarenko.dao.dto.user.UserDto::getEmail)
                .containsExactlyInAnyOrder("user2@test.com", "user3@test.com");
    }

    @Test
    void testGetAllFriends_WhenNoFriends() {
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'user2@test.com'", Long.class);

        List<com.alonazarenko.dao.dto.user.UserDto> friends = userRepository.getAllFriends(userId);

        assertThat(friends).isEmpty();
    }


    @Test
    void testDeleteUser() {
        Long userId = jdbcTemplate.queryForObject(
                "SELECT user_id FROM users WHERE email = 'user1@test.com'",
                Long.class
        );

        boolean isDeleted = userRepository.delete(userId);

        assertThat(isDeleted).isTrue();

        Optional<User> retrievedUser = userRepository.getById(userId);
        assertThat(retrievedUser).isEmpty();

        Collection<User> remainingUsers = userRepository.getAll();
        assertThat(remainingUsers).hasSize(2);
    }

    @Test
    void testDeleteUser_WhenNotFound() {
        boolean isDeleted = userRepository.delete(999L);

        assertThat(isDeleted).isFalse();
    }

    @Test
    void testDeleteUser_CascadeDeletesFriends() {
        Long userId = jdbcTemplate.queryForObject(
                "SELECT user_id FROM users WHERE email = 'user1@test.com'",
                Long.class
        );

        boolean isDeleted = userRepository.delete(userId);

        assertThat(isDeleted).isTrue();

        Optional<User> retrievedUser = userRepository.getById(userId);
        assertThat(retrievedUser).isEmpty();

        Integer friendsCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friendships WHERE user_id = ? OR friend_id = ?",
                Integer.class, userId, userId
        );
        assertThat(friendsCount).isZero();
    }
}
