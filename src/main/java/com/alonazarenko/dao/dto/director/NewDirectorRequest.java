package com.alonazarenko.dao.dto.director;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewDirectorRequest {

    @NotBlank(message = "Director name cannot be blank")
    private String name;
}
