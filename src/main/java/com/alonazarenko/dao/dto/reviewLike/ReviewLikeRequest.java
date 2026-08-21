package com.alonazarenko.dao.dto.reviewLike;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ReviewLikeRequest {
    @NotNull(message = "Review ID cannot be null")
    @Positive(message = "Review ID must be positive")
    private Long reviewId;

    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotNull(message = "Reaction type is required (true - like, false - dislike)")
    private Boolean isLike;
}