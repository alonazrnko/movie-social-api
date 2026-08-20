package com.alonazarenko.service;

import com.alonazarenko.dao.dto.user.NewUserRequest;
import com.alonazarenko.dao.dto.user.UpdateUserRequest;
import com.alonazarenko.dao.dto.user.UserDto;
import com.alonazarenko.dao.dto.user.UserMapper;
import com.alonazarenko.dao.repository.FriendshipRepository;
import com.alonazarenko.dao.repository.UserRepository;
import com.alonazarenko.exception.InternalServerException;
import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public UserDto create(NewUserRequest request) {
        log.info("Create user login={}", request.getLogin());

        User user = UserMapper.mapToUser(request);
        user = userRepository.create(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto update(long userId, UpdateUserRequest request) {
        log.debug("Updating user id={}", request.getId());

        User updatedUser = userRepository.getById(userId)
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        updatedUser = userRepository.update(updatedUser);

        log.info("User updated successfully id={}", request.getId());
        return UserMapper.mapToUserDto(updateCollections(updatedUser, updatedUser.getId()));
    }

    public Collection<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(user -> updateCollections(user, user.getId()))
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public void delete(long id) {
        getById(id);
        boolean deleted = userRepository.delete(id);
        if (!deleted) {
            throw new InternalServerException("Failed to delete user with id=" + id);
        }
    }

    public UserDto getById(long id) {
        User user = userRepository.getById(id)
                .orElseThrow(() -> {
                    log.warn("User not found id={}", id);
                    return new NotFoundException("User not found");
                });

        return UserMapper.mapToUserDto(updateCollections(user, id));
    }

    public User updateCollections(User user, long userId) {
        user.setFriends(friendshipRepository.findAllByUserId(userId));
        return user;
    }
}
