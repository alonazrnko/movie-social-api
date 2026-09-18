package com.alonazarenko.dao.dto.director;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DirectorDto {
    @NotNull(message = "Director ID cannot be null")
    @Positive(message = "Director ID must be positive")
    private Long id;

    @NotBlank(message = "Director name cannot be blank")
    private String name;
}
