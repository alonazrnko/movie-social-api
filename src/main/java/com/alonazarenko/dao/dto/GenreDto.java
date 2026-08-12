package com.alonazarenko.dao.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GenreDto {
    @NotNull
    private Long id;
}