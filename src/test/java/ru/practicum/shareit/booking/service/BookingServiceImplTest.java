package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.enums.BookingStateEnum;
import ru.practicum.shareit.enums.RequestStateEnum;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
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
class BookingServiceImplTest {

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
        UserDto secondUserDto = userService.saveUser(new UserDto(2L, "secondName", "second@mail.ru"));
        BookingRequestDto bookingRequestDto =
                new BookingRequestDto(itemDto.getId(),
                        LocalDateTime.now().minusHours(2).truncatedTo(ChronoUnit.SECONDS),
                        LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS));
        BookingResponseDto bookingResponseDto = bookingService.saveBooking(secondUserDto.getId(), bookingRequestDto);
        assertThat(bookingResponseDto.getId()).isEqualTo(1L);
        assertThat(bookingResponseDto.getStart()).isEqualTo(bookingRequestDto.getStart());
        assertThat(bookingResponseDto.getEnd()).isEqualTo(bookingRequestDto.getEnd());
        assertThat(bookingResponseDto.getItem().getId()).isEqualTo(itemDto.getId());
        assertThat(bookingResponseDto.getItem().getName()).isEqualTo(itemDto.getName());
        assertThat(bookingResponseDto.getItem().getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(bookingResponseDto.getItem().getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(bookingResponseDto.getBooker().getId()).isEqualTo(secondUserDto.getId());
        assertThat(bookingResponseDto.getBooker().getName()).isEqualTo(secondUserDto.getName());
        assertThat(bookingResponseDto.getBooker().getEmail()).isEqualTo(secondUserDto.getEmail());
        assertThat(bookingResponseDto.getStatus()).isEqualTo(BookingStateEnum.WAITING);

        bookingResponseDto = bookingService.considerBooking(itemDto.getId(),
                true, bookingResponseDto.getId());
        assertThat(bookingResponseDto.getId()).isEqualTo(1L);
        assertThat(bookingResponseDto.getStart()).isEqualTo(bookingRequestDto.getStart());
        assertThat(bookingResponseDto.getEnd()).isEqualTo(bookingRequestDto.getEnd());
        assertThat(bookingResponseDto.getItem().getId()).isEqualTo(itemDto.getId());
        assertThat(bookingResponseDto.getItem().getName()).isEqualTo(itemDto.getName());
        assertThat(bookingResponseDto.getItem().getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(bookingResponseDto.getItem().getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(bookingResponseDto.getBooker().getId()).isEqualTo(secondUserDto.getId());
        assertThat(bookingResponseDto.getBooker().getName()).isEqualTo(secondUserDto.getName());
        assertThat(bookingResponseDto.getBooker().getEmail()).isEqualTo(secondUserDto.getEmail());
        assertThat(bookingResponseDto.getStatus()).isEqualTo(BookingStateEnum.APPROVED);

        bookingResponseDto = bookingService.getBooking(secondUserDto.getId(), bookingResponseDto.getId());
        assertThat(bookingResponseDto.getId()).isEqualTo(1L);
        assertThat(bookingResponseDto.getStart()).isEqualTo(bookingRequestDto.getStart());
        assertThat(bookingResponseDto.getEnd()).isEqualTo(bookingRequestDto.getEnd());
        assertThat(bookingResponseDto.getItem().getId()).isEqualTo(itemDto.getId());
        assertThat(bookingResponseDto.getItem().getName()).isEqualTo(itemDto.getName());
        assertThat(bookingResponseDto.getItem().getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(bookingResponseDto.getItem().getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(bookingResponseDto.getBooker().getId()).isEqualTo(secondUserDto.getId());
        assertThat(bookingResponseDto.getBooker().getName()).isEqualTo(secondUserDto.getName());
        assertThat(bookingResponseDto.getBooker().getEmail()).isEqualTo(secondUserDto.getEmail());
        assertThat(bookingResponseDto.getStatus()).isEqualTo(BookingStateEnum.APPROVED);

        List<BookingResponseDto> bookingResponseDtoList = bookingService.getAllUserBookings(secondUserDto.getId(),
                RequestStateEnum.ALL, 0, 5);
        assertThat(bookingResponseDtoList.size()).isEqualTo(1);
        assertThat(bookingResponseDtoList.get(0).getId()).isEqualTo(1L);
        assertThat(bookingResponseDtoList.get(0).getStart()).isEqualTo(bookingRequestDto.getStart());
        assertThat(bookingResponseDtoList.get(0).getEnd()).isEqualTo(bookingRequestDto.getEnd());
        assertThat(bookingResponseDtoList.get(0).getItem().getId()).isEqualTo(itemDto.getId());
        assertThat(bookingResponseDtoList.get(0).getItem().getName()).isEqualTo(itemDto.getName());
        assertThat(bookingResponseDtoList.get(0).getItem().getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(bookingResponseDtoList.get(0).getItem().getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(bookingResponseDtoList.get(0).getBooker().getId()).isEqualTo(secondUserDto.getId());
        assertThat(bookingResponseDtoList.get(0).getBooker().getName()).isEqualTo(secondUserDto.getName());
        assertThat(bookingResponseDtoList.get(0).getBooker().getEmail()).isEqualTo(secondUserDto.getEmail());
        assertThat(bookingResponseDtoList.get(0).getStatus()).isEqualTo(BookingStateEnum.APPROVED);

        bookingResponseDtoList = bookingService.getAllItemsBooking(itemDto.getId(),
                RequestStateEnum.ALL, 0, 5);
        assertThat(bookingResponseDtoList.size()).isEqualTo(1);
        assertThat(bookingResponseDtoList.get(0).getId()).isEqualTo(1L);
        assertThat(bookingResponseDtoList.get(0).getStart()).isEqualTo(bookingRequestDto.getStart());
        assertThat(bookingResponseDtoList.get(0).getEnd()).isEqualTo(bookingRequestDto.getEnd());
        assertThat(bookingResponseDtoList.get(0).getItem().getId()).isEqualTo(itemDto.getId());
        assertThat(bookingResponseDtoList.get(0).getItem().getName()).isEqualTo(itemDto.getName());
        assertThat(bookingResponseDtoList.get(0).getItem().getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(bookingResponseDtoList.get(0).getItem().getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(bookingResponseDtoList.get(0).getBooker().getId()).isEqualTo(secondUserDto.getId());
        assertThat(bookingResponseDtoList.get(0).getBooker().getName()).isEqualTo(secondUserDto.getName());
        assertThat(bookingResponseDtoList.get(0).getBooker().getEmail()).isEqualTo(secondUserDto.getEmail());
        assertThat(bookingResponseDtoList.get(0).getStatus()).isEqualTo(BookingStateEnum.APPROVED);
    }
}