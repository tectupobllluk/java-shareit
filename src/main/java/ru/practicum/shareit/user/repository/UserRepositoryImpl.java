package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private final Set<String> emailUniqSet = new HashSet<>();
    private long generatedId = 0;

    private Long generateId() {
        return ++generatedId;
    }

    @Override
    public Set<String> getEmailUniqSet() {
        return emailUniqSet;
    }

    @Override
    public void addEmailToSet(String email) {
        emailUniqSet.add(email);
    }

    @Override
    public void removeEmailFromSet(String email) {
        emailUniqSet.remove(email);
    }

    @Override
    public User saveUser(User user) {
        user.setId(generateId());
        emailUniqSet.add(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
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
