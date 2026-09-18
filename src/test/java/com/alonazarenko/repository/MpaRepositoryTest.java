package com.alonazarenko.repository;

import com.alonazarenko.dao.repository.MpaRepository;
import com.alonazarenko.dao.repository.mappers.MpaRowMapper;
import com.alonazarenko.model.MpaRating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
class MpaRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MpaRepository mpaRepository;

    @BeforeEach
    void setUp() {
        MpaRowMapper mapper = new MpaRowMapper();
        mpaRepository = new MpaRepository(jdbcTemplate, mapper);

        jdbcTemplate.update("DELETE FROM mpa_ratings");

        jdbcTemplate.update("INSERT INTO mpa_ratings (mpa_id, name) VALUES (1, 'G')");
        jdbcTemplate.update("INSERT INTO mpa_ratings (mpa_id, name) VALUES (2, 'PG')");
        jdbcTemplate.update("INSERT INTO mpa_ratings (mpa_id, name) VALUES (3, 'PG-13')");
        jdbcTemplate.update("INSERT INTO mpa_ratings (mpa_id, name) VALUES (4, 'R')");
        jdbcTemplate.update("INSERT INTO mpa_ratings (mpa_id, name) VALUES (5, 'NC-17')");
    }

    @Test
    void testFindById() {
        Optional<MpaRating> mpaRating = mpaRepository.findById(1L);

        assertThat(mpaRating).isPresent();
        assertThat(mpaRating.get().getId()).isEqualTo(1L);
        assertThat(mpaRating.get().getName()).isEqualTo("G");
    }

    @Test
    void testFindById_WhenNotFound() {
        Optional<MpaRating> mpaRating = mpaRepository.findById(999L);

        assertThat(mpaRating).isEmpty();
    }

    @Test
    void testFindAll() {
        List<MpaRating> mpaRatings = mpaRepository.findAll();

        assertThat(mpaRatings).hasSize(5);
        assertThat(mpaRatings).extracting(MpaRating::getName)
                .containsExactlyInAnyOrder("G", "PG", "PG-13", "R", "NC-17");

        assertThat(mpaRatings.get(0).getId()).isEqualTo(1L);
        assertThat(mpaRatings.get(1).getId()).isEqualTo(2L);
        assertThat(mpaRatings.get(2).getId()).isEqualTo(3L);
        assertThat(mpaRatings.get(3).getId()).isEqualTo(4L);
        assertThat(mpaRatings.get(4).getId()).isEqualTo(5L);
    }
}
