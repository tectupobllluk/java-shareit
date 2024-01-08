package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto saveUser(UserDto userDto);

    UserDto updateUser(UserDto user, Long userId);

    List<UserDto> getAllUsers();

    UserDto getUser(long userId);

    void deleteUser(long userId);
}
