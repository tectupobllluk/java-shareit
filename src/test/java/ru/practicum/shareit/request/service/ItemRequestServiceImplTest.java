package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest
class ItemRequestServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    void integrationTest() {
        UserDto userDto = userService.saveUser(new UserDto(1L, "userName", "mail@mail.ru"));
        UserDto secondUserDto = userService.saveUser(new UserDto(2L, "secondUserName", "second@mail.ru"));
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "requestDescription");
        ItemDto itemDto = new ItemDto(1L, "FirstItem", "firstDescription", true,
                itemRequestDto.getId());

        ItemRequestResponseDto itemResponseDto = itemRequestService.saveItem(userDto.getId(), itemRequestDto);
        assertThat(itemResponseDto.getId()).isEqualTo(1L);
        assertThat(itemResponseDto.getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(itemResponseDto.getItems().size()).isEqualTo(0);
        assertThat(itemResponseDto.getCreated()).isBefore(LocalDateTime.now());

        itemService.saveItem(userDto.getId(), itemDto);

        List<ItemRequestResponseDto> responseDtoList = itemRequestService.getAllOwnerRequests(userDto.getId());
        assertThat(responseDtoList.size()).isEqualTo(1);
        assertThat(responseDtoList.get(0).getId()).isEqualTo(1L);
        assertThat(responseDtoList.get(0).getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(responseDtoList.get(0).getItems().size()).isEqualTo(1);
        assertThat(responseDtoList.get(0).getItems().get(0).getId()).isEqualTo(itemDto.getId());
        assertThat(responseDtoList.get(0).getItems().get(0).getName()).isEqualTo(itemDto.getName());
        assertThat(responseDtoList.get(0).getItems().get(0).getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(responseDtoList.get(0).getItems().get(0).getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(responseDtoList.get(0).getItems().get(0).getRequestId()).isEqualTo(itemRequestDto.getId());
        assertThat(responseDtoList.get(0).getCreated()).isBefore(LocalDateTime.now());

        responseDtoList = itemRequestService.getAllRequests(secondUserDto.getId(), 0, 5);
        assertThat(responseDtoList.size()).isEqualTo(1);
        assertThat(responseDtoList.get(0).getId()).isEqualTo(1L);
        assertThat(responseDtoList.get(0).getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(responseDtoList.get(0).getItems().size()).isEqualTo(1);
        assertThat(responseDtoList.get(0).getItems().get(0).getId()).isEqualTo(itemDto.getId());
        assertThat(responseDtoList.get(0).getItems().get(0).getName()).isEqualTo(itemDto.getName());
        assertThat(responseDtoList.get(0).getItems().get(0).getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(responseDtoList.get(0).getItems().get(0).getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(responseDtoList.get(0).getItems().get(0).getRequestId()).isEqualTo(itemRequestDto.getId());
        assertThat(responseDtoList.get(0).getCreated()).isBefore(LocalDateTime.now());

        itemResponseDto = itemRequestService.getRequestById(userDto.getId(), 1L);
        assertThat(itemResponseDto.getId()).isEqualTo(1L);
        assertThat(itemResponseDto.getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(itemResponseDto.getItems().size()).isEqualTo(1);
        assertThat(itemResponseDto.getItems().get(0).getId()).isEqualTo(itemDto.getId());
        assertThat(itemResponseDto.getItems().get(0).getName()).isEqualTo(itemDto.getName());
        assertThat(itemResponseDto.getItems().get(0).getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(itemResponseDto.getItems().get(0).getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(itemResponseDto.getItems().get(0).getRequestId()).isEqualTo(itemRequestDto.getId());
        assertThat(itemResponseDto.getCreated()).isBefore(LocalDateTime.now());
    }
}