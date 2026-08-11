package com.alonazarenko.storage.friend;

import com.alonazarenko.model.Friendship;

import java.util.Collection;
import java.util.Optional;

public interface FriendshipStorage {

    void save(Friendship friendship);

    void delete(long userId, long friendId);

    Collection<Friendship> findAllByUserId(long userId);
}
