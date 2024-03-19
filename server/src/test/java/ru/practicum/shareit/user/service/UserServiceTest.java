package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private final UserDto userDto = new UserDto(1L, "name", "email@email.ru");
    private final User user = new User(1L, "name", "email@email.ru");

    @Test
    void saveUser() {
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto newUser = userService.saveUser(userDto);

        assertEquals(newUser, userDto);
        assertThat(newUser)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("email", "email@email.ru");
    }

    @Test
    void updateUser() {
        when(userRepository.findById(100L))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> userService
                .updateUser(userDto, 100L));
        assertEquals("User not found with 100 id!", exception.getMessage());

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto newUser = userService.updateUser(userDto, 1L);

        assertEquals(newUser, userDto);
        assertThat(newUser)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("email", "email@email.ru");
    }

    @Test
    void getAllUsers() {
        User secondUser = new User(2L, "secondName", "second@second.ru");
        UserDto secondUserDto = UserMapper.toUserDto(secondUser);
        when(userRepository.findAll())
                .thenReturn(List.of(user, secondUser));

        List<UserDto> userDtoList = userService.getAllUsers();

        assertThat(userDtoList.size()).isEqualTo(2);
        assertEquals(List.of(userDto, secondUserDto), userDtoList);
    }

    @Test
    void getUser() {
        when(userRepository.findById(100L))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUser(100L));
        assertThat(exception.getMessage()).isEqualTo("User not found with id = 100");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        assertThat(userService.getUser(1L)).isEqualTo(userDto);
    }

    @Test
    void deleteUser() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.deleteUser(100L));
        assertThat(exception.getMessage()).isEqualTo("User not found with id = 100");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(anyLong());
    }
}