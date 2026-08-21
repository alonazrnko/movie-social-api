package com.alonazarenko.dao.dto.reviewLike;

import com.alonazarenko.model.ReviewLike;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReviewLikeMapper {

    public static ReviewLike mapToReviewLike(ReviewLikeRequest request) {
        return new ReviewLike(
                request.getReviewId(),
                request.getUserId(),
                request.getIsLike()
        );
    }

    public static ReviewLikeDto mapToReviewLikeDto(ReviewLike reviewLike) {
        return new ReviewLikeDto(
                reviewLike.getReviewId(),
                reviewLike.getUserId(),
                reviewLike.getIsLike()
        );
    }
}
