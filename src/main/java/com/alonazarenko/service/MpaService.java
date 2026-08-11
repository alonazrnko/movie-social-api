package com.alonazarenko.service;

import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.MpaRating;
import com.alonazarenko.storage.mpa.MpaStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<MpaRating> getAll() {
        return mpaStorage.getAll();
    }

    public MpaRating getById(int id) {
        return mpaStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("MPA not found"));
    }
}