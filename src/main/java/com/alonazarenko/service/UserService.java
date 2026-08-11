package com.alonazarenko.service;

import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.User;
import com.alonazarenko.storage.user.UserStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User create(User user) {
        log.debug("Creating user login={}", user.getLogin());
        return userStorage.create(user);
    }

    public User update(User user) {
        log.debug("Updating user id={}", user.getId());

        userStorage.getById(user.getId())
                .orElseThrow(() -> {
                    log.warn("User with id={} not found for update", user.getId());
                    return new NotFoundException(
                            "User with id " + user.getId() + " not found"
                    );
                });

        User updatedUser = userStorage.update(user);
        log.info("User updated successfully id={}", user.getId());
        return updatedUser;
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> {
                    log.warn("User not found id={}", id);
                    return new NotFoundException("User not found");
                });
    }

    public void addFriend(long userId, long friendId) {
        log.info("Adding friend: userId={}, friendId={}", userId, friendId);

        User user = userStorage.getById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", userId);
                    return new NotFoundException("User with id " + userId + " not found");
                });

        User friend = userStorage.getById(friendId)
                .orElseThrow(() -> {
                    log.warn("Friend not found: id={}", friendId);
                    return new NotFoundException("User with id " + friendId + " not found");
                });

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        log.info("Users {} and {} are now friends", userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        log.info("Removing friend: userId={}, friendId={}", userId, friendId);

        User user = userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        User friend = userStorage.getById(friendId)
                .orElseThrow(() -> new NotFoundException("User with id " + friendId + " not found"));

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Friendship removed: {} <-> {}", userId, friendId);
    }

    public Collection<User> getFriends(long userId) {
        log.info("Getting friends for userId={}", userId);

        User user = userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        return user.getFriends().stream()
                .map(id -> userStorage.getById(id)
                        .orElseThrow(() -> new NotFoundException("User with id " + id + " not found")))
                .toList();
    }

    public Collection<User> getCommonFriends(long id, long otherId) {
        User user = getById(id);
        User other = getById(otherId);

        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(this::getById)
                .collect(Collectors.toList());
    }
}
