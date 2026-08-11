package com.alonazarenko.storage.friend;

import com.alonazarenko.model.Friendship;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {

    private final Map<String, Friendship> friendships = new HashMap<>();

    private String key(long userId, long friendId) {
        return userId + "_" + friendId;
    }

    @Override
    public void save(Friendship friendship) {
        friendships.put(
                key(friendship.getUserId(), friendship.getFriendId()),
                friendship
        );
    }

    @Override
    public void update(Friendship friendship) {
        save(friendship);
    }

    @Override
    public void delete(long userId, long friendId) {
        friendships.remove(key(userId, friendId));
        friendships.remove(key(friendId, userId));
    }

    @Override
    public Optional<Friendship> find(long userId, long friendId) {
        return Optional.ofNullable(friendships.get(key(userId, friendId)));
    }

    @Override
    public Collection<Friendship> findAllByUserId(long userId) {
        return friendships.values().stream()
                .filter(f -> f.getUserId() == userId || f.getFriendId() == userId)
                .collect(Collectors.toList());
    }
}