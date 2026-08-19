package com.alonazarenko.dao.repository;

import com.alonazarenko.dao.repository.mappers.MpaRowMapper;
import com.alonazarenko.model.MpaRating;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public class MpaRepository extends BaseRepository<MpaRating> {
    private static final String FIND_ALL_SQL = "SELECT * FROM mpa_ratings";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM mpa_ratings WHERE mpa_id = ?";
    private static final String EXISTS_BY_ID = "SELECT EXISTS(SELECT 1 FROM mpa_ratings WHERE mpa_id = ?)";

    public MpaRepository(JdbcTemplate jdbc, MpaRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Optional<MpaRating> findById(Long id) {
        return findOne(FIND_BY_ID_SQL, id);
    }

    public List<MpaRating> findAll() {
        return findMany(FIND_ALL_SQL);
    }

    public boolean existsById(long id) {
        return exists(EXISTS_BY_ID, id);
    }
}
