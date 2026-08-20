package com.alonazarenko.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLike {
    private Long reviewId;
    private Long userId;
    private Boolean isLike;
}
