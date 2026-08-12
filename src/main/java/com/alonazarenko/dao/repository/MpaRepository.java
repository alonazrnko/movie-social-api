package com.alonazarenko.dao.repository;

import com.alonazarenko.dao.repository.mappers.MpaRowMapper;
import com.alonazarenko.model.Film;
import com.alonazarenko.model.MpaRating;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public class MpaRepository extends BaseRepository<MpaRating> {
    private static final String FIND_ALL_SQL = "SELECT * FROM mpa_rating";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM mpa_rating WHERE rating_id = ?";
    private static final String FIND_ALL_BY_ID_SQL = "SELECT id, name FROM mpa_ratings WHERE id = ?";

    public MpaRepository(JdbcTemplate jdbc, MpaRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Optional<MpaRating> findById(Long id) {
        return findOne(FIND_BY_ID_SQL, id);
    }

    public List<MpaRating> findAll() {
        return findMany(FIND_ALL_SQL);
    }

    private void loadMpa(Film film) {
        if (film.getMpa() == null) {
            return;
        }

        List<MpaRating> list = jdbc.query(FIND_ALL_BY_ID_SQL, mapper, film.getMpa().getId());

        if (!list.isEmpty()) {
            film.setMpa(list.get(0));
        }
    }
}
