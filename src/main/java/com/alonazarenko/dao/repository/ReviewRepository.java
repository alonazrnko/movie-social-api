package com.alonazarenko.dao.repository;

import com.alonazarenko.dao.repository.mappers.ReviewRowMapper;
import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.Review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepository extends BaseRepository<Review> {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE reviews SET content = ?, is_positive = ?, useful = ? WHERE review_id = ?";
    private static final String UPDATE_USEFUL = "UPDATE reviews SET useful = ? WHERE review_id = ?";
    private static final String DELETE_SQL = "DELETE FROM reviews WHERE review_id = ?";
    private static final String EXISTS_BY_ID = "SELECT COUNT(*) > 0 FROM reviews WHERE review_id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
    private static final String FIND_BY_FILM_ID_SQL = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";

    public ReviewRepository(JdbcTemplate jdbc, ReviewRowMapper mapper) {
        super(jdbc, mapper);
        this.jdbcTemplate = jdbc;
    }

    public Review create(Review review) {
        long id = insert(
                INSERT_SQL,
                review.getContent(),
                review.isPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful()
        );
        review.setReviewId(id);
        return review;
    }

    public Review update(Review review) {
        update(
                UPDATE_SQL,
                review.getContent(),
                review.isPositive(),
                review.getUseful(),
                review.getReviewId()
        );

        return review;
    }

    public Review save(Review review) {
        if (review.getReviewId() == 0) {
            return create(review);
        } else {
            return update(review);
        }
    }

    public Optional<Review> findById(Long reviewId) {
        return findOne(FIND_BY_ID_SQL, reviewId);
    }

    public void delete(Long reviewId) {
        boolean deleted = delete(DELETE_SQL, reviewId);
        if (!deleted) {
            throw new NotFoundException("Review with ID=" + reviewId + " not found");
        }
    }

    public List<Review> findByFilmId(Long filmId, Integer count) {
        return findMany(FIND_BY_FILM_ID_SQL, filmId, count);
    }

    public List<Review> findAll(Integer count) {
        return findMany(FIND_ALL_SQL, count);
    }

    public boolean existsById(Long reviewId) {
        return exists(EXISTS_BY_ID, reviewId);
    }

    public void updateUseful(Long reviewId, Long useful) {
        jdbcTemplate.update(UPDATE_USEFUL, reviewId, useful);
    }
}
