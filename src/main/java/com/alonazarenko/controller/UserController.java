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

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Create user login={}", user.getLogin());
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Update user id={}", user.getId());
        return userService.update(user);
    }

    @GetMapping
    public Collection<User> getAll() {
        log.debug("Get all users");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable long id) {
        log.debug("Get user id={}", id);
        return userService.getById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Add friend {} -> {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Remove friend {} -> {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        log.debug("Get friends for user id={}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.debug("Get common friends {} & {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}