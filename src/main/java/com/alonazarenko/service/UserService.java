package com.alonazarenko.service;

import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.Friendship;
import com.alonazarenko.model.User;
import com.alonazarenko.storage.friend.FriendshipStorage;
import com.alonazarenko.storage.user.UserStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public User create(User user) {
        log.info("Create user login={}", user.getLogin());
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
        log.info("Adding friend: {} -> {}", userId, friendId);

        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        userStorage.getById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found: " + friendId));

        friendshipStorage.save(new Friendship(userId, friendId));
    }

    public void removeFriend(long userId, long friendId) {
        log.info("Remove friend {} -> {}", userId, friendId);

        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        userStorage.getById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found: " + friendId));

        friendshipStorage.delete(userId, friendId);
    }

    public Collection<User> getFriends(long userId) {
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        return friendshipStorage.findAllByUserId(userId).stream()
                .map(Friendship::getFriendId)
                .map(id -> userStorage.getById(id).orElseThrow())
                .toList();
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        Set<Long> userFriends = friendshipStorage.findAllByUserId(userId).stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());

        Set<Long> otherFriends = friendshipStorage.findAllByUserId(otherId).stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());

        userFriends.retainAll(otherFriends);

        return userFriends.stream()
                .map(id -> userStorage.getById(id).orElseThrow())
                .toList();
    }
}
