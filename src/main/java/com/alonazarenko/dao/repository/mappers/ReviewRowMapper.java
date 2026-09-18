package com.alonazarenko.dao.repository.mappers;

import com.alonazarenko.model.Review;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewRowMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(resultSet.getLong("review_id"));
        review.setContent(resultSet.getString("content"));
        review.setPositive(resultSet.getBoolean("is_positive"));
        review.setUserId(resultSet.getLong("user_id"));
        review.setFilmId(resultSet.getLong("film_id"));
        review.setUseful(resultSet.getLong("useful"));

        return review;
    }
}
