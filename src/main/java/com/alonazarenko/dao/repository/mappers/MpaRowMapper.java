package com.alonazarenko.dao.repository.mappers;

import com.alonazarenko.model.MpaRating;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRowMapper implements RowMapper<MpaRating> {

    @Override
    public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        MpaRating mpa = new MpaRating();

        mpa.setId(rs.getLong("mpa_id"));
        mpa.setName(rs.getString("name"));

        return mpa;
    }
}