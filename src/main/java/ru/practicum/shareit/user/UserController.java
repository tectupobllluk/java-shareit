package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody @Validated(Create.class) UserDto userDto) {
        log.info("Create user: {} - Started!", userDto);
        UserDto user = userService.saveUser(userDto);
        log.info("Create user: {} - Finished!", user);
        return user;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody @Validated(Update.class) UserDto userDto, @PathVariable Long userId) {
        log.info("Update user: {} - Started!", userDto);
        UserDto user = userService.updateUser(userDto, userId);
        log.info("Update user: {} - Finished!", user);
        return user;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Get all users - Started!");
        List<UserDto> users = userService.getAllUsers();
        log.info("Get all users: {} - Finished!", users);
        return users;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        log.info("Get user by id {} - Started!", id);
        UserDto user = userService.getUser(id);
        log.info("Get user by id: {} - Finished!", user);
        return user;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("Delete user by id {} - Started!", id);
        userService.deleteUser(id);
        log.info("Delete user by id {} - Finished!", id);
    }
}
