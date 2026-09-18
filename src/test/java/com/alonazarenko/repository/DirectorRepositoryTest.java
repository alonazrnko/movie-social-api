package com.alonazarenko.repository;

import com.alonazarenko.dao.dto.director.DirectorDto;
import com.alonazarenko.dao.dto.director.NewDirectorRequest;
import com.alonazarenko.dao.repository.DirectorRepository;
import com.alonazarenko.dao.repository.mappers.DirectorRowMapper;
import com.alonazarenko.model.Director;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;


import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
class DirectorRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private DirectorRepository directorRepository;

    @BeforeEach
    void setUp() {
        directorRepository = new DirectorRepository(jdbcTemplate, new DirectorRowMapper());

        jdbcTemplate.update("DELETE FROM film_directors");
        jdbcTemplate.update("DELETE FROM directors");
        jdbcTemplate.update("DELETE FROM films");

        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration, mpa_id) " +
                        "VALUES (1, 'Film', 'Desc', '2000-01-01', 120, 1)"
        );
    }

    @Test
    void testFindAll() {
        insertDirector("Zed");
        insertDirector("Alex");

        List<Director> directors = directorRepository.findAll();

        assertThat(directors).hasSize(2);
        assertThat(directors)
                .extracting(Director::getName)
                .containsExactly("Zed", "Alex");
    }

    @Test
    void testFindById_WhenExists() {
        long id = insertDirector("Nolan");

        Optional<Director> director = directorRepository.findById(id);

        assertThat(director).isPresent();
        assertThat(director.get().getName()).isEqualTo("Nolan");
    }

    @Test
    void testFindById_WhenNotExists() {
        assertThat(directorRepository.findById(999L)).isEmpty();
    }

    @Test
    void testCreateDirector() {
        NewDirectorRequest request = new NewDirectorRequest();
        request.setName("Spielberg");

        Director saved = directorRepository.create(request);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Spielberg");
    }

    @Test
    void testUpdateDirector() {
        NewDirectorRequest request = new NewDirectorRequest();
        request.setName("Old Name");

        Director created = directorRepository.create(request);

        DirectorDto updateDto = new DirectorDto();
        updateDto.setId(created.getId());
        updateDto.setName("New Name");

        Director updated = directorRepository.update(updateDto);

        Director fromDb = directorRepository.findById(created.getId()).orElseThrow();
        assertThat(fromDb.getName()).isEqualTo("New Name");
    }

    @Test
    void testUpdateDirectorWithoutId_shouldThrowException() {
        DirectorDto dto = new DirectorDto();
        dto.setName("No ID");

        assertThatThrownBy(() -> directorRepository.update(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Director ID must not be null");
    }

    @Test
    void testDelete() {
        long id = insertDirector("To delete");

        boolean deleted = directorRepository.delete(id);

        assertThat(deleted).isTrue();
        assertThat(directorRepository.findById(id)).isEmpty();
    }

    @Test
    void testExists() {
        long id = insertDirector("Exists");

        assertThat(directorRepository.existsById(id)).isTrue();
        assertThat(directorRepository.existsById(999L)).isFalse();
    }

    @Test
    void testFindDirectorsByFilmId() {
        long directorId = insertDirector("Kubrick");

        jdbcTemplate.update(
                "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)",
                1L, directorId
        );

        List<Director> directors = directorRepository.findDirectorsByFilmId(1L);

        assertThat(directors).hasSize(1);
        assertThat(directors.get(0).getName()).isEqualTo("Kubrick");
    }

    @Test
    void testAddDirectorToFilm() {
        long directorId = insertDirector("Scott");

        directorRepository.addDirectorToFilm(1L, directorId);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_directors WHERE film_id = ? AND director_id = ?",
                Integer.class,
                1L,
                directorId
        );

        assertThat(count).isEqualTo(1);
    }

    @Test
    void testAddDirectorsToFilm() {
        long d1 = insertDirector("One");
        long d2 = insertDirector("Two");

        directorRepository.addDirectorsToFilm(1L, Set.of(d1, d2));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_directors WHERE film_id = ?",
                Integer.class,
                1L
        );

        assertThat(count).isEqualTo(2);
    }

    @Test
    void testDeleteDirectorsFromFilm() {
        long directorId = insertDirector("Remove");

        jdbcTemplate.update(
                "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)",
                1L, directorId
        );

        Integer before = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_directors WHERE film_id = ?",
                Integer.class,
                1L
        );
        assertThat(before).isEqualTo(1);

        directorRepository.deleteDirectorsFromFilm(1L);

        Integer after = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_directors WHERE film_id = ?",
                Integer.class,
                1L
        );
        assertThat(after).isZero();
    }

    @Test
    void testFilmDirectorExists() {
        long directorId = insertDirector("Check");

        jdbcTemplate.update(
                "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)",
                1L, directorId
        );

        assertThat(directorRepository.filmDirectorExists(1L, directorId)).isTrue();
        assertThat(directorRepository.filmDirectorExists(1L, 999L)).isFalse();
    }

    private long insertDirector(String name) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO directors (name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, name);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
