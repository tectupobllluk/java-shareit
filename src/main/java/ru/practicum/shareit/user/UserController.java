package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public User createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Create user: {} - Started!", userDto);
        User user = UserMapper.toUser(userDto);
        userService.saveUser(user);
        log.info("Create user: {} - Finished!", user);
        return user;
    }

    @PatchMapping("/{userId}")
    public User updateUser(@RequestBody @Valid UserDto userDto, @PathVariable Long userId) {
        log.info("Update user: {} - Started!", userDto);
        User user = userService.updateUser(userDto, userId);
        log.info("Update user: {} - Finished!", user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUser(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}
