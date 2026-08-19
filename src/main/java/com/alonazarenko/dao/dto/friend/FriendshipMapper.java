package com.alonazarenko.dao.dto.friend;

import com.alonazarenko.model.Friendship;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FriendshipMapper {

    public static Friendship mapToFriendship(Long userId, NewFriendshipRequest request) {
        Friendship friendship = new Friendship();
        friendship.setUserId(userId);
        friendship.setFriendId(request.getFriendId());
        return friendship;
    }

    public static FriendshipDto mapToFriendDto(Friendship friendship) {
        FriendshipDto dto = new FriendshipDto();
        dto.setUserId(friendship.getUserId());
        dto.setFriendId(friendship.getFriendId());
        return dto;
    }
}
