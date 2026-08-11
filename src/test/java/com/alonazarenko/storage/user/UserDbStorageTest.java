package com.alonazarenko.storage.user;

import com.alonazarenko.model.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    private User savedUser;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("test@mail.com")
                .login("tester")
                .name("Test")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        savedUser = userStorage.create(user);
    }

    @Test
    void shouldFindUserById() {
        Optional<User> userOptional = userStorage.getById(savedUser.getId());

        assertThat(userOptional).isPresent();
    }

    @Test
    void shouldCreateUser() {
        jdbcTemplate.update("DELETE FROM users");

        User user = User.builder()
                .email("new@mail.com")
                .login("newlogin")
                .name("New User")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User saved = userStorage.create(user);

        assertThat(saved.getId()).isPositive();

        Optional<User> fromDb = userStorage.getById(saved.getId());
        assertThat(fromDb).isPresent();
    }

    @Test
    void shouldUpdateUser() {
        User user = userStorage.getById(1).orElseThrow();
        user.setName("Updated Name");

        userStorage.update(user);

        User updated = userStorage.getById(1).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    void shouldReturnAllUsers() {
        assertThat(userStorage.getAll())
                .isNotEmpty()
                .hasSize(1);
    }
}
