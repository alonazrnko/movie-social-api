package com.alonazarenko.service;

import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.Friendship;
import com.alonazarenko.model.FriendshipStatus;
import com.alonazarenko.model.User;
import com.alonazarenko.storage.friend.FriendshipStorage;
import com.alonazarenko.storage.user.UserStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

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
        log.info("Sending friend request: {} -> {}", userId, friendId);

        userStorage.getById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", userId);
                    return new NotFoundException("User with id " + userId + " not found");
                });

        userStorage.getById(friendId)
                .orElseThrow(() -> {
                    log.warn("Friend not found: id={}", friendId);
                    return new NotFoundException("User with id " + friendId + " not found");
                });

        friendshipStorage.find(friendId, userId)
                .ifPresentOrElse(
                        existing -> {
                            existing.setStatus(FriendshipStatus.CONFIRMED);
                            friendshipStorage.update(existing);

                            friendshipStorage.save(
                                    new Friendship(userId, friendId, FriendshipStatus.CONFIRMED)
                            );

                            log.info("Friendship confirmed between {} and {}", userId, friendId);
                        },
                        () -> friendshipStorage.save(
                                new Friendship(userId, friendId, FriendshipStatus.PENDING)
                        )
                );
    }

    public void removeFriend(long userId, long friendId) {
        log.info("Removing friendship between {} and {}", userId, friendId);
        friendshipStorage.delete(userId, friendId);
    }

    public Collection<User> getFriends(long userId) {
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        return friendshipStorage.findAllByUserId(userId).stream()
                .filter(f -> f.getStatus() == FriendshipStatus.CONFIRMED)
                .map(f -> f.getUserId() == userId ? f.getFriendId() : f.getUserId())
                .map(id -> userStorage.getById(id).orElseThrow())
                .toList();
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        log.info("Getting common friends between {} and {}", userId, otherId);

        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        userStorage.getById(otherId)
                .orElseThrow(() -> new NotFoundException("User not found: " + otherId));

        Set<Long> userFriends = friendshipStorage.findAllByUserId(userId).stream()
                .filter(f -> f.getStatus() == FriendshipStatus.CONFIRMED)
                .map(f -> f.getUserId() == userId ? f.getFriendId() : f.getUserId())
                .collect(Collectors.toSet());

        Set<Long> otherFriends = friendshipStorage.findAllByUserId(otherId).stream()
                .filter(f -> f.getStatus() == FriendshipStatus.CONFIRMED)
                .map(f -> f.getUserId() == otherId ? f.getFriendId() : f.getUserId())
                .collect(Collectors.toSet());

        userFriends.retainAll(otherFriends);

        return userFriends.stream()
                .map(id -> userStorage.getById(id).orElseThrow())
                .toList();
    }
}
