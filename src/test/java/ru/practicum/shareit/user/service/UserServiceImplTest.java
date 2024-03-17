package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    void integrationTest() {
        UserDto userDto = new UserDto(1L, "FirstUser", "mail@mail.ru");
        userService.saveUser(userDto);
        UserDto result = userService.getUser(1L);
        assertThat(result).isEqualTo(userDto);

        UserDto updateUser = new UserDto(1L, "newUserName", "newMail@mail.ru");
        userService.updateUser(updateUser, 1L);
        result = userService.getUser(1L);
        assertThat(result).isEqualTo(updateUser);

        UserDto secondUser = new UserDto(2L, "secondName", "second@mail.ru");
        userService.saveUser(secondUser);
        List<UserDto> resultList = userService.getAllUsers();
        assertThat(resultList.size()).isEqualTo(2);
        assertThat(resultList.get(0)).isEqualTo(updateUser);
        assertThat(resultList.get(1)).isEqualTo(secondUser);

        userService.deleteUser(2);
        resultList = userService.getAllUsers();
        assertThat(resultList.size()).isEqualTo(1);
        assertThat(resultList.get(0)).isEqualTo(updateUser);
    }
}