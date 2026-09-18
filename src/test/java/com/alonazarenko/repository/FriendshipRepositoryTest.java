package com.alonazarenko.repository;

import com.alonazarenko.dao.repository.FriendshipRepository;
import com.alonazarenko.dao.repository.mappers.FriendshipRowMapper;
import com.alonazarenko.model.Friendship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
class FriendshipRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private FriendshipRepository friendshipRepository;

    @BeforeEach
    void setUp() {
        FriendshipRowMapper mapper = new FriendshipRowMapper();
        friendshipRepository = new FriendshipRepository(jdbcTemplate, mapper);

        jdbcTemplate.update("DELETE FROM friendships");
        jdbcTemplate.update("DELETE FROM users");

        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login) VALUES (1, 'user1@mail.ru', 'user1')"
        );
        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login) VALUES (2, 'user2@mail.ru', 'user2')"
        );
        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login) VALUES (3, 'user3@mail.ru', 'user3')"
        );
    }

    @Test
    void testAddFriendship() {
        Friendship friendship = new Friendship(1L, 2L);

        Friendship saved = friendshipRepository.add(friendship);

        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getFriendId()).isEqualTo(2L);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?",
                Integer.class,
                1L, 2L
        );

        assertThat(count).isEqualTo(1);
    }

    @Test
    void testFindAllByUserId() {
        jdbcTemplate.update("INSERT INTO friendships (user_id, friend_id) VALUES (1, 2)");
        jdbcTemplate.update("INSERT INTO friendships (user_id, friend_id) VALUES (1, 3)");

        List<Friendship> friendships = friendshipRepository.findAllByUserId(1L);

        assertThat(friendships)
                .hasSize(2)
                .extracting(Friendship::getFriendId)
                .containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    void testFindAllByUserId_WhenNoFriends() {
        List<Friendship> friendships = friendshipRepository.findAllByUserId(1L);

        assertThat(friendships).isEmpty();
    }

    @Test
    void testDeleteFriendship() {
        jdbcTemplate.update("INSERT INTO friendships (user_id, friend_id) VALUES (1, 2)");

        friendshipRepository.delete(1L, 2L);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?",
                Integer.class,
                1L, 2L
        );

        assertThat(count).isEqualTo(0);
    }
}
