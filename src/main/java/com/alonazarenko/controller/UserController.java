package com.alonazarenko.controller;

import com.alonazarenko.dao.dto.user.NewUserRequest;
import com.alonazarenko.dao.dto.user.UpdateUserRequest;
import com.alonazarenko.dao.dto.user.UserDto;
import com.alonazarenko.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody NewUserRequest request) {
        log.info("Create user login={}", request.getLogin());
        return userService.create(request);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserRequest request) {
        log.info("Update user id={}", request.getId());

        if (request.getName() == null || request.getName().isBlank()) {
            request.setName(request.getLogin());
        }

        return userService.update(request.getId(), request);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.debug("Get all users");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.debug("Get user id={}", id);
        return userService.getById(id);
    }
}