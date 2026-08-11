package com.alonazarenko.storage.user;

import com.alonazarenko.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    User update(User user);

    Optional<User> getById(long id);

    Collection<User> getAll();
}
