package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private BookingService bookingService;

    @Test
    void integrationTest() {
        UserDto userDto = userService.saveUser(new UserDto(1L, "userName", "mail@mail.ru"));
        ItemRequestResponseDto itemRequestDto = itemRequestService.saveItem(userDto.getId(),
                new ItemRequestDto(1L, "requestDescription"));
        ItemDto itemDto = new ItemDto(1L, "FirstItem", "firstDescription", true,
                itemRequestDto.getId());
        itemService.saveItem(userDto.getId(), itemDto);
        ItemResponseDto result = itemService.getItem(userDto.getId(), itemDto.getId());
        assertThat(result.getId()).isEqualTo(itemDto.getId());
        assertThat(result.getName()).isEqualTo(itemDto.getName());
        assertThat(result.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(result.getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(result.getRequestId()).isEqualTo(itemDto.getRequestId());

        ItemDto updateItem = new ItemDto(1L, "newName", "newDescription", true, 1L);
        itemService.updateItem(userDto.getId(), updateItem, result.getId());
        result = itemService.getItem(itemDto.getId(), updateItem.getId());
        assertThat(result.getId()).isEqualTo(updateItem.getId());
        assertThat(result.getName()).isEqualTo(updateItem.getName());
        assertThat(result.getDescription()).isEqualTo(updateItem.getDescription());
        assertThat(result.getAvailable()).isEqualTo(updateItem.getAvailable());
        assertThat(result.getRequestId()).isEqualTo(updateItem.getRequestId());

        ItemDto secondItem = new ItemDto(2L, "secondName", "secondDescription",
                true, 1L);
        itemService.saveItem(userDto.getId(), secondItem);
        List<ItemResponseDto> resultList = itemService.getAllOwnerItems(userDto.getId(), 0, 5);
        assertThat(resultList.size()).isEqualTo(2);
        assertThat(resultList.get(0).getId()).isEqualTo(updateItem.getId());
        assertThat(resultList.get(0).getName()).isEqualTo(updateItem.getName());
        assertThat(resultList.get(0).getDescription()).isEqualTo(updateItem.getDescription());
        assertThat(resultList.get(0).getAvailable()).isEqualTo(updateItem.getAvailable());
        assertThat(resultList.get(0).getRequestId()).isEqualTo(updateItem.getRequestId());
        assertThat(resultList.get(1).getId()).isEqualTo(secondItem.getId());
        assertThat(resultList.get(1).getName()).isEqualTo(secondItem.getName());
        assertThat(resultList.get(1).getDescription()).isEqualTo(secondItem.getDescription());
        assertThat(resultList.get(1).getAvailable()).isEqualTo(secondItem.getAvailable());
        assertThat(resultList.get(1).getRequestId()).isEqualTo(secondItem.getRequestId());

        resultList = itemService.searchItem(userDto.getId(), "new", 0, 5);
        assertThat(resultList.size()).isEqualTo(1);
        assertThat(resultList.get(0).getId()).isEqualTo(updateItem.getId());
        assertThat(resultList.get(0).getName()).isEqualTo(updateItem.getName());
        assertThat(resultList.get(0).getDescription()).isEqualTo(updateItem.getDescription());
        assertThat(resultList.get(0).getAvailable()).isEqualTo(updateItem.getAvailable());
        assertThat(resultList.get(0).getRequestId()).isEqualTo(updateItem.getRequestId());

        UserDto secondUserDto = new UserDto(2L, "secondName", "second@mail.ru");
        userService.saveUser(secondUserDto);
        BookingRequestDto bookingRequestDto =
                new BookingRequestDto(itemDto.getId(),
                        LocalDateTime.now().minusHours(2).truncatedTo(ChronoUnit.SECONDS),
                        LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS));
        BookingResponseDto bookingResponseDto = bookingService.saveBooking(secondUserDto.getId(), bookingRequestDto);
        CommentDto comment = new CommentDto(1L, "text", secondUserDto.getName(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        CommentDto commentDto = itemService.addComment(secondUserDto.getId(), itemDto.getId(), comment);
        commentDto.setCreated(comment.getCreated());
        assertThat(commentDto).isEqualTo(comment);
    }
}