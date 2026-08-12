package com.alonazarenko.dao.dto.friend;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateFriendshipRequest {
    @NotNull(message = "user ID cannot be empty")
    @Positive(message = "user ID must be positive")
    private Long userId;

    @NotNull(message = "friend ID cannot be empty")
    @Positive(message = "friend ID must be positive")
    private Long friendId;
    private String status;
}
