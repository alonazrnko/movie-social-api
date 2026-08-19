package com.alonazarenko.controller;

import com.alonazarenko.dao.dto.user.UserDto;
import com.alonazarenko.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class FriendController {
    private final FriendshipService friendshipService;

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Add friend {} -> {}", id, friendId);
        friendshipService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Remove friend {} -> {}", id, friendId);
        friendshipService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> getFriends(@PathVariable long id) {
        log.debug("Get friends for user id={}", id);
        return friendshipService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.debug("Get common friends {} & {}", id, otherId);
        return friendshipService.getCommonFriends(id, otherId);
    }
}
