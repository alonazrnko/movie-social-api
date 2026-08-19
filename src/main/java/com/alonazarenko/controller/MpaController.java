package com.alonazarenko.controller;

import com.alonazarenko.dao.dto.mpa.MpaDto;
import com.alonazarenko.service.MpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public List<MpaDto> getAll() {
        return mpaService.getAll();
    }

    @GetMapping("/{id}")
    public MpaDto getById(@PathVariable long id) {
        return mpaService.getById(id);
    }
}
