package com.alonazarenko.service;

import com.alonazarenko.dao.dto.user.UserDto;
import com.alonazarenko.dao.repository.FriendshipRepository;
import com.alonazarenko.dao.repository.UserRepository;
import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.Friendship;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public void addFriend(long userId, long friendId) {
        log.info("Adding friend: {} -> {}", userId, friendId);

        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        userRepository.getById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found: " + friendId));

        Friendship friendship = new Friendship();
        friendship.setUserId(userId);
        friendship.setFriendId(friendId);

        friendshipRepository.add(friendship);
    }

    public void removeFriend(long userId, long friendId) {
        log.info("Remove friend {} -> {}", userId, friendId);

        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        userRepository.getById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found: " + friendId));

        friendshipRepository.delete(userId, friendId);
    }

    public Collection<UserDto> getFriends(long userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        return userRepository.getAllFriends(userId);
    }

    public Collection<UserDto> getCommonFriends(long userId, long otherId) {
        Collection<UserDto> userFriends = getFriends(userId);
        Collection<UserDto> otherUserFriends = getFriends(otherId);

        Set<Long> userFriendIds = userFriends.stream()
                .map(UserDto::getId)
                .collect(Collectors.toSet());

        return otherUserFriends.stream()
                .filter(friend -> userFriendIds.contains(friend.getId()))
                .collect(Collectors.toList());
    }
}
