package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.BadEmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private static final HashMap<Long, User> users = new HashMap<>();
    private static long generatedId = 0;

    private Long generateId() {
        return ++generatedId;
    }

    private void checkEmail(String email, Long userId) {
        for (User userForCheck : getAllUsers()) {
            if (userForCheck.getEmail().equals(email) && !userForCheck.getId().equals(userId)) {
                throw new BadEmailException("User with " + email + " email is already created!");
            }
        }
    }

    private void checkEmail(String email) {
        checkEmail(email, null);
    }

    @Override
    public void saveUser(User user) {
        checkEmail(user.getEmail());
        user.setId(generateId());
        users.put(user.getId(), user);
    }

    @Override
    public User updateUser(UserDto userDto, Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User not found with " + userId + " id!");
        }
        User userToUpdate = users.get(userId);
        if (userDto.getEmail() != null) {
            checkEmail(userDto.getEmail(), userId);
            userToUpdate.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            userToUpdate.setName(userDto.getName());
        }
        return userToUpdate;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUser(long userId) {
        if (!users.containsKey(userId)) {
            return Optional.empty();
        }
        return Optional.of(users.get(userId));
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }
}
