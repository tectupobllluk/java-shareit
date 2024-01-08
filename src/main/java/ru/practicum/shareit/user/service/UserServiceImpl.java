package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exceptions.BadEmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (userRepository.getEmailUniqSet().contains(user.getEmail())) {
            throw new BadEmailException("User with " + user.getEmail() + " email is already created!");
        }
        return UserMapper.toUserDto(userRepository.saveUser(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User userToUpdate = userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId + " id!"));
        if (userDto.getEmail() != null) {
            if (userRepository.getEmailUniqSet().contains(userDto.getEmail()) &&
                    !userDto.getEmail().equals(userToUpdate.getEmail())) {
                throw new BadEmailException("User with " + userDto.getEmail() + " email is already created!");
            }
            userRepository.removeEmailFromSet(userToUpdate.getEmail());
            userRepository.addEmailToSet(userDto.getEmail());
            userToUpdate.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            userToUpdate.setName(userDto.getName());
        }
        return UserMapper.toUserDto(userRepository.updateUser(userToUpdate));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(long userId) {
        return UserMapper.toUserDto(userRepository.getUser(userId)
        .orElseThrow(() -> new NotFoundException("User not found with id = " + userId)));
    }

    @Override
    public void deleteUser(long userId) {
        User user = userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId));
        userRepository.removeEmailFromSet(user.getEmail());
        userRepository.deleteUser(userId);
    }
}
