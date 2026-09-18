package com.alonazarenko.dao.dto.like;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class NewLikeRequest {
    @NotNull(message = "film ID cannot be empty")
    @Positive(message = "film ID must be positive")
    private Long filmId;

    @NotNull(message = "user ID cannot be empty")
    @Positive(message = "user ID must be positive")
    private Long userId;
}
