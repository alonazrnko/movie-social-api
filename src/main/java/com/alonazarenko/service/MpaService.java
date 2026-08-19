package com.alonazarenko.service;

import com.alonazarenko.dao.dto.mpa.MpaDto;
import com.alonazarenko.dao.dto.mpa.MpaMapper;
import com.alonazarenko.dao.repository.MpaRepository;
import com.alonazarenko.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaRepository mpaRepository;

    public List<MpaDto> getAll() {
        return mpaRepository.findAll().stream()
                .map(MpaMapper::mapToMpaDto)
                .toList();
    }

    public MpaDto getById(long mpaId) {
        return mpaRepository.findById(mpaId)
                .map(MpaMapper::mapToMpaDto)
                .orElseThrow(() -> new NotFoundException("MPA with ID " + mpaId + " not found"));

    }

    public void validateMpaExists(long mpaId) {
        if (!mpaRepository.existsById(mpaId)) {
            throw new NotFoundException("MPA with id " + mpaId + " not found");
        }
    }
}