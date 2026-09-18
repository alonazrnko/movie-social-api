package com.alonazarenko.repository;

import com.alonazarenko.dao.repository.ReviewLikeRepository;
import com.alonazarenko.dao.repository.ReviewRepository;
import com.alonazarenko.dao.repository.mappers.ReviewLikeRowMapper;
import com.alonazarenko.dao.repository.mappers.ReviewRowMapper;
import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.Review;
import com.alonazarenko.model.ReviewLike;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
class ReviewLikeRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ReviewLikeRepository reviewLikeRepository;
    private ReviewRepository reviewRepository;

    private Long reviewId;

    @BeforeEach
    void setUp() {
        reviewLikeRepository = new ReviewLikeRepository(jdbcTemplate, new ReviewLikeRowMapper());
        reviewRepository = new ReviewRepository(jdbcTemplate, new ReviewRowMapper());

        jdbcTemplate.update("DELETE FROM review_likes");
        jdbcTemplate.update("DELETE FROM reviews");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");

        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) VALUES " +
                        "(1, 'u1@mail.ru', 'u1', 'User1', '1990-01-01')," +
                        "(2, 'u2@mail.ru', 'u2', 'User2', '1990-01-01')"
        );

        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration, mpa_id) " +
                        "VALUES (1, 'Film', 'Desc', '2000-01-01', 120, 1)"
        );

        Review review = new Review();
        review.setContent("Review");
        review.setPositive(true);
        review.setUserId(1L);
        review.setFilmId(1L);
        review.setUseful(0L);

        reviewId = reviewRepository.create(review).getReviewId();
    }

    @Test
    void testSave_CreatePath() {
        ReviewLike like = buildLike(reviewId, 2L, true);

        reviewLikeRepository.save(like);

        Optional<ReviewLike> fromDb =
                reviewLikeRepository.findByReviewIdAndUserId(reviewId, 2L);

        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getIsLike()).isTrue();
        assertThat(fromDb.get().getUserId()).isEqualTo(2L);
        assertThat(fromDb.get().getReviewId()).isEqualTo(reviewId);
    }

    @Test
    void testSave_UpdatePath() {
        reviewLikeRepository.addLike(buildLike(reviewId, 2L, true));

        ReviewLike updated = buildLike(reviewId, 2L, false);
        reviewLikeRepository.save(updated);

        ReviewLike fromDb =
                reviewLikeRepository.findByReviewIdAndUserId(reviewId, 2L).orElseThrow();

        assertThat(fromDb.getIsLike()).isFalse();
    }

    @Test
    void testFindByReviewIdAndUserId_WhenExists() {
        reviewLikeRepository.addLike(buildLike(reviewId, 2L, true));

        Optional<ReviewLike> like =
                reviewLikeRepository.findByReviewIdAndUserId(reviewId, 2L);

        assertThat(like).isPresent();
        assertThat(like.get().getIsLike()).isTrue();
    }

    @Test
    void testFindByReviewIdAndUserId_WhenNotExists() {
        Optional<ReviewLike> like =
                reviewLikeRepository.findByReviewIdAndUserId(reviewId, 999L);

        assertThat(like).isEmpty();
    }

    @Test
    void testDeleteLike() {
        reviewLikeRepository.addLike(buildLike(reviewId, 2L, true));

        reviewLikeRepository.delete(reviewId, 2L);

        assertThat(
                reviewLikeRepository.findByReviewIdAndUserId(reviewId, 2L)
        ).isEmpty();
    }

    @Test
    void testDelete_WhenNotFound() {
        assertThatThrownBy(() -> reviewLikeRepository.delete(reviewId, 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testCountLikesAndDislikes() {
        reviewLikeRepository.addLike(buildLike(reviewId, 1L, true));
        reviewLikeRepository.addLike(buildLike(reviewId, 2L, false));

        assertThat(reviewLikeRepository.countLikes(reviewId)).isEqualTo(1);
        assertThat(reviewLikeRepository.countDislikes(reviewId)).isEqualTo(1);
    }

    @Test
    void testExists() {
        reviewLikeRepository.addLike(buildLike(reviewId, 2L, true));

        assertThat(reviewLikeRepository.existsById(reviewId, 2L)).isTrue();
        assertThat(reviewLikeRepository.existsById(reviewId, 999L)).isFalse();
    }

    @Test
    void testUpdateReviewUseful() {
        reviewLikeRepository.updateReviewUseful(reviewId, 5L);

        Review review = reviewRepository.findById(reviewId).orElseThrow();
        assertThat(review.getUseful()).isEqualTo(5L);
    }

    private ReviewLike buildLike(Long reviewId, Long userId, boolean isLike) {
        ReviewLike like = new ReviewLike();
        like.setReviewId(reviewId);
        like.setUserId(userId);
        like.setIsLike(isLike);
        return like;
    }
}
