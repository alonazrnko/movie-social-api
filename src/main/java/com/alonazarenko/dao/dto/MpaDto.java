package com.alonazarenko.dao.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MpaDto {

    @NotNull
    private Long id;
}
