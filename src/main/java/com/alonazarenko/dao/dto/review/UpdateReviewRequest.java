package com.alonazarenko.dao.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateReviewRequest {
    @NotNull(message = "Review ID is required")
    @Positive(message = "Review ID must be positive")
    private Long reviewId;

    @NotBlank(message = "Review content cannot be blank")
    private String content;

    @NotNull(message = "Review type is required")
    private Boolean isPositive;

    public boolean hasContent() {
        return content != null && !content.isBlank();
    }

    public boolean hasIsPositive() {
        return isPositive != null;
    }
}
