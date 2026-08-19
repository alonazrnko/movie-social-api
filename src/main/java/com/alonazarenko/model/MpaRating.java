package com.alonazarenko.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MpaRating {
    @NotNull(message = "MPA Rating ID is required")
    private long id;
    private String name;
}
