package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public void saveUser(@Valid User user) {
        userRepository.saveUser(user);
    }

    @Override
    public User updateUser(UserDto userDto, Long userId) {
        return userRepository.updateUser(userDto, userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User getUser(long userId) {
        return userRepository.getUser(userId)
        .orElseThrow(() -> new NotFoundException("User not found with id = " + userId));
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId));
        userRepository.deleteUser(userId);
    }
}
