package com.alonazarenko.controller;

import com.alonazarenko.model.User;
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

    // CREATE
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("POST /users — create user login={}", user.getLogin());
        return userService.create(user);
    }

    // UPDATE
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("PUT /users — update user id={}", user.getId());
        return userService.update(user);
    }

    // READ ALL
    @GetMapping
    public Collection<User> getAll() {
        log.debug("GET /users");
        return userService.getAll();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public User getById(@PathVariable long id) {
        log.debug("GET /users/{}", id);
        return userService.getById(id);
    }

    // FRIENDS

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("PUT /users/{}/friends/{}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("DELETE /users/{}/friends/{}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        log.debug("GET /users/{}/friends", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.debug("GET /users/{}/friends/common/{}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}