package com.alonazarenko.storage.mpa;

import com.alonazarenko.model.MpaRating;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRowMapper implements RowMapper<MpaRating> {

    @Override
    public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new MpaRating(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}