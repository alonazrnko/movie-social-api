package com.alonazarenko.repository;

import com.alonazarenko.dao.repository.LikeRepository;
import com.alonazarenko.dao.repository.mappers.LikeRowMapper;
import com.alonazarenko.model.Like;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
class LikeRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LikeRepository likeRepository;

    @BeforeEach
    void setUp() {
        LikeRowMapper mapper = new LikeRowMapper();
        likeRepository = new LikeRepository(jdbcTemplate, mapper);

        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");

        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login) VALUES (1, 'user1@mail.ru', 'user1')"
        );
        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login) VALUES (2, 'user2@mail.ru', 'user2')"
        );

        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration, mpa_id) " +
                        "VALUES (1, 'Film', 'Description', '2000-01-01', 120, 1)"
        );
    }

    @Test
    void testAddLike() {
        Like like = new Like(1L, 1L);

        likeRepository.addLike(like);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?",
                Integer.class,
                1L, 1L
        );

        assertThat(count).isEqualTo(1);
    }

    @Test
    void testFindUserIdsByFilmId() {
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (1, 1)");
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (1, 2)");

        Set<Long> userIds = likeRepository.findUserIdsByFilmId(1L);

        assertThat(userIds)
                .hasSize(2)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void testFindUserIdsByFilmId_WhenNoLikes() {
        Set<Long> userIds = likeRepository.findUserIdsByFilmId(1L);

        assertThat(userIds).isEmpty();
    }

    @Test
    void testRemoveLike() {
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (1, 1)");

        likeRepository.removeLike(1L, 1L);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?",
                Integer.class,
                1L, 1L
        );

        assertThat(count).isEqualTo(0);
    }
}
