package com.alonazarenko.dao.repository;

import com.alonazarenko.dao.repository.mappers.ReviewLikeRowMapper;
import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.ReviewLike;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ReviewLikeRepository extends BaseRepository<ReviewLike> {
    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_BY_REVIEW_AND_USER_SQL = "SELECT * FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String INSERT_SQL = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE review_likes SET is_like = ? WHERE review_id = ? AND user_id = ?";
    private static final String UPDATE_REVIEW_USEFUL = "UPDATE reviews SET useful = ? WHERE review_id = ?";
    private static final String DELETE_SQL = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String COUNT_LIKES_SQL = "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_like = true";
    private static final String COUNT_DISLIKES_SQL = "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_like = false";
    private static final String EXISTS_BY_ID = "SELECT COUNT(*) > 0 FROM review_likes WHERE review_id = ? AND user_id = ?";

    public ReviewLikeRepository(JdbcTemplate jdbc, ReviewLikeRowMapper mapper) {
        super(jdbc, mapper);
        this.jdbcTemplate = jdbc;
    }

    public Optional<ReviewLike> findByReviewIdAndUserId(Long reviewId, Long userId) {
        return findOne(FIND_BY_REVIEW_AND_USER_SQL, reviewId, userId);
    }

    public void save(ReviewLike reviewLike) {
        if (existsById(reviewLike.getReviewId(), reviewLike.getUserId())) {
            update(reviewLike);
        } else {
            addLike(reviewLike);
        }
    }

    public void addLike(ReviewLike reviewLike) {
        update(INSERT_SQL,
                reviewLike.getReviewId(),
                reviewLike.getUserId(),
                reviewLike.getIsLike());
    }

    public ReviewLike update(ReviewLike reviewLike) {
        int rowsUpdated = jdbcTemplate.update(
                UPDATE_SQL,
                reviewLike.getIsLike(),
                reviewLike.getReviewId(),
                reviewLike.getUserId());

        if (rowsUpdated == 0) {
            throw new NotFoundException(
                    "Reaction to review " + reviewLike.getReviewId() +
                            " from user " + reviewLike.getUserId() + " not found");
        }

        return reviewLike;
    }

    public void delete(Long reviewId, Long userId) {
        boolean deleted = delete(DELETE_SQL, reviewId, userId);
        if (!deleted) {
            throw new NotFoundException(
                    "Reaction to review " + reviewId + " from user " + userId + " not found");
        }
    }

    public long countLikes(Long reviewId) {
        Long count = jdbcTemplate.queryForObject(COUNT_LIKES_SQL, Long.class, reviewId);
        return count != null ? count : 0L;
    }

    public long countDislikes(Long reviewId) {
        Long count = jdbcTemplate.queryForObject(COUNT_DISLIKES_SQL, Long.class, reviewId);
        return count != null ? count : 0L;
    }

    public boolean existsById(Long reviewId, Long userId) {
        return exists(EXISTS_BY_ID, reviewId, userId);
    }

    public void updateReviewUseful(Long reviewId, Long useful) {
        jdbcTemplate.update(UPDATE_REVIEW_USEFUL, useful, reviewId);
    }
}
