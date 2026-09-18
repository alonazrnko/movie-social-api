package com.alonazarenko.dao.dto.review;

import com.alonazarenko.model.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class ReviewMapper {

    public Review mapToReview(NewReviewRequest request) {
        Review review = new Review();
        review.setContent(request.getContent());
        review.setPositive(request.getIsPositive());
        review.setUserId(request.getUserId());
        review.setFilmId(request.getFilmId());
        review.setUseful(0L);
        return review;
    }

    public ReviewDto mapToReviewDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setContent(review.getContent());
        dto.setIsPositive(review.isPositive());
        dto.setUserId(review.getUserId());
        dto.setFilmId(review.getFilmId());
        dto.setUseful(review.getUseful());
        return dto;
    }

    public Review updateReviewFields(Review review, UpdateReviewRequest request) {
        if (request.hasContent()) {
            review.setContent(request.getContent());
        }
        if (request.hasIsPositive()) {
            review.setPositive(request.getIsPositive());
        }
        return review;
    }
}
