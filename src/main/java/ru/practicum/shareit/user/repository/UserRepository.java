package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void saveUser(User user);

    User updateUser(UserDto user, Long userId);

    List<User> getAllUsers();

    Optional<User> getUser(long userId);

    void deleteUser(long userId);
}
