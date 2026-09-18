package com.alonazarenko.service;

import com.alonazarenko.dao.dto.review.NewReviewRequest;
import com.alonazarenko.dao.dto.review.ReviewDto;
import com.alonazarenko.dao.dto.review.ReviewMapper;
import com.alonazarenko.dao.dto.review.UpdateReviewRequest;
import com.alonazarenko.dao.repository.ReviewRepository;
import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.Review;
import com.alonazarenko.model.enums.EventOperation;
import com.alonazarenko.model.enums.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserService userService;
    private final FilmService filmService;
    private final ReviewLikeService reviewLikeService;
    private final EventService eventService;

    public ReviewDto createReview(NewReviewRequest request) {
        userService.getById(request.getUserId());
        filmService.getById(request.getFilmId());

        Review review = reviewMapper.mapToReview(request);
        Review savedReview = reviewRepository.save(review);

        log.info("Creating review id={} for the movie id={} from user id={}",
                savedReview.getReviewId(), savedReview.getFilmId(), savedReview.getUserId());

        eventService.addEvent(
                savedReview.getUserId(),
                EventType.REVIEW,
                EventOperation.ADD,
                savedReview.getReviewId()
        );

        return reviewMapper.mapToReviewDto(savedReview);
    }

    public ReviewDto updateReview(UpdateReviewRequest request) {
        Review existingReview = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new NotFoundException("Review with id=" + request.getReviewId() + " not found"));

        Review updatedReview = reviewMapper.updateReviewFields(existingReview, request);
        Review savedReview = reviewRepository.update(updatedReview);

        log.info("Updating review id={}", savedReview.getReviewId());

        eventService.addEvent(
                savedReview.getUserId(),
                EventType.REVIEW,
                EventOperation.UPDATE,
                savedReview.getReviewId()
        );

        return reviewMapper.mapToReviewDto(savedReview);
    }

    public void deleteReview(Long reviewId) {
        Review deletedReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review with id=" + reviewId + " not found"));

        reviewRepository.delete(reviewId);

        log.info("Deleting review id={}", reviewId);

        eventService.addEvent(
                deletedReview.getUserId(),
                EventType.REVIEW,
                EventOperation.REMOVE,
                reviewId
        );
    }

    public ReviewDto getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review with id=" + reviewId + " not found"));

        review.setUseful(reviewLikeService.getUseful(reviewId));

        return reviewMapper.mapToReviewDto(review);
    }

    public List<ReviewDto> getReviewsByFilmId(Long filmId, Integer count) {
        filmService.getById(filmId);

        List<Review> reviews = reviewRepository.findByFilmId(filmId, count);

        reviews.forEach(review ->
                review.setUseful(reviewLikeService.getUseful(review.getReviewId())));

        return reviews.stream()
                .map(reviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    public List<ReviewDto> getAllReviews(Integer count) {
        List<Review> reviews = reviewRepository.findAll(count);

        reviews.forEach(review ->
                review.setUseful(reviewLikeService.getUseful(review.getReviewId())));

        return reviews.stream()
                .map(reviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }
}
