package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    User saveUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    Optional<User> getUser(long userId);

    void deleteUser(long userId);

    Set<String> getEmailUniqSet();

    void addEmailToSet(String email);

    void removeEmailFromSet(String email);
}
