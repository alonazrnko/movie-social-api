package com.alonazarenko.storage.mpa;

import com.alonazarenko.model.MpaRating;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MpaRating> getAll() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new MpaRating(rs.getInt("id"), rs.getString("name")));
    }

    @Override
    public Optional<MpaRating> getById(long id) {
        String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                        new MpaRating(rs.getInt("id"), rs.getString("name")), id)
                .stream()
                .findFirst();
    }
}
