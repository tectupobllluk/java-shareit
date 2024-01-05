package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserService {

    void saveUser(@Valid User user);

    User updateUser(UserDto user, Long userId);

    List<User> getAllUsers();

    User getUser(long userId);

    void deleteUser(long userId);
}
