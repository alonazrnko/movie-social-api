package com.alonazarenko.storage.mpa;

import com.alonazarenko.model.MpaRating;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {
    List<MpaRating> getAll();
    Optional<MpaRating> getById(long id);
}
