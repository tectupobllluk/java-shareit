package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.BookingStateEnum;
import ru.practicum.shareit.enums.RequestStateEnum;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class BookingServiceTest {

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private BookingService bookingService;

    private final User owner = new User(1L, "name", "email@email.ru");
    private final User secondOwner = new User(2L, "secondName", "secondEmail@email.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "description", owner,
            LocalDateTime.now().minusHours(3).truncatedTo(ChronoUnit.SECONDS));
    private final Item item = new Item(1L, "itemName", "itemDescription",
            true, owner, itemRequest);
    private final Item unavailableItem = new Item(1L, "itemName", "itemDescription",
            false, owner, itemRequest);
    private final Booking booking = new Booking(1L, LocalDateTime.now().minusHours(1)
            .truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS), item,
            owner, BookingStateEnum.APPROVED, LocalDateTime.now().minusHours(2).truncatedTo(ChronoUnit.SECONDS));
    private final Booking waitingBooking = new Booking(1L, LocalDateTime.now().minusHours(1)
            .truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS), item,
            owner, BookingStateEnum.WAITING, LocalDateTime.now().minusHours(2).truncatedTo(ChronoUnit.SECONDS));
    private final BookingRequestDto bookingRequestDto =
            new BookingRequestDto(1L, LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS),
                    LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
    private final BookingResponseDto bookingResponseDto = new BookingResponseDto(1L, LocalDateTime.now().minusHours(1)
            .truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS),
            new ItemDto(1L, "itemName", "itemDescription", true, 1L),
            new UserDto(1L, "name", "email@email.ru"), BookingStateEnum.APPROVED);

    @Test
    void saveBooking() {
        when(userRepository.findById(100L))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.saveBooking(100L, bookingRequestDto));
        assertThat(exception.getMessage()).isEqualTo("User with id - 100 not found");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException itemException = assertThrows(NotFoundException.class,
                () -> bookingService.saveBooking(1L, bookingRequestDto));
        assertThat(itemException.getMessage()).isEqualTo("Item with id - 1 not found");

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        final NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.saveBooking(1L, bookingRequestDto));
        assertThat(notFoundException.getMessage()).isEqualTo("Owner can't book his own item");

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(unavailableItem));

        final BadRequestException badRequestException = assertThrows(BadRequestException.class,
                () -> bookingService.saveBooking(2L, bookingRequestDto));
        assertThat(badRequestException.getMessage())
                .isEqualTo("Item " + unavailableItem + " unavailable for booking");

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        final BookingRequestDto wrongBookingRequestDto =
                new BookingRequestDto(1L, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                        LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS));

        final BadRequestException badRequestExceptionWrongDto = assertThrows(BadRequestException.class,
                () -> bookingService.saveBooking(2L, wrongBookingRequestDto));
        assertThat(badRequestExceptionWrongDto.getMessage())
                .isEqualTo("Timestamps must not be equal or end before start");

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingResponseDto newBookingResponseDto = bookingService.saveBooking(2L, bookingRequestDto);

        assertThat(newBookingResponseDto).isEqualTo(bookingResponseDto);
    }

    @Test
    void considerBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.considerBooking(100L, true, 1L));
        assertThat(exception.getMessage()).isEqualTo("User with id - 100 not found");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException bookingException = assertThrows(NotFoundException.class,
                () -> bookingService.considerBooking(1L, true, 1L));
        assertThat(bookingException.getMessage()).isEqualTo("Booking with id - 1 not found");

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        final BadRequestException bookingConsideredException = assertThrows(BadRequestException.class,
                () -> bookingService.considerBooking(1L, true, 1L));
        assertThat(bookingConsideredException.getMessage())
                .isEqualTo("Owner can't change booking status after its considering");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(secondOwner));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(waitingBooking));

        final NotFoundException bookingOwnerException = assertThrows(NotFoundException.class,
                () -> bookingService.considerBooking(1L, true, 1L));
        assertThat(bookingOwnerException.getMessage()).isEqualTo("Requester and booking owner not equals");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.save(any()))
                .thenReturn(waitingBooking);

        BookingResponseDto newBookingResponseDto = bookingService.considerBooking(1L, true, 1L);
        assertThat(newBookingResponseDto.getStatus()).isEqualTo(BookingStateEnum.APPROVED);
        assertThat(newBookingResponseDto).isEqualTo(bookingResponseDto);

        waitingBooking.setStatus(BookingStateEnum.WAITING);
        BookingResponseDto responseDto = bookingService.considerBooking(1L, false, 1L);
        assertThat(responseDto.getStatus()).isEqualTo(BookingStateEnum.REJECTED);
        bookingResponseDto.setStatus(BookingStateEnum.REJECTED);
        assertThat(responseDto).isEqualTo(bookingResponseDto);
    }

    @Test
    void getBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(100L, 1L));
        assertThat(exception.getMessage()).isEqualTo("User with id - 100 not found");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException bookingException = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(1L, 1L));
        assertThat(bookingException.getMessage()).isEqualTo("Booking with id - 1 not found");

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        booking.setBooker(secondOwner);
        item.setOwner(secondOwner);

        final NotFoundException bookingRequesterException = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(1L, 1L));
        assertThat(bookingRequesterException.getMessage())
                .isEqualTo("Requester and booking or item owner not equals");

        booking.setBooker(owner);

        BookingResponseDto newBookingResponseDto = bookingService.getBooking(1L, 1L);
        assertThat(newBookingResponseDto).isEqualTo(bookingResponseDto);
    }

    @Test
    void getAllUserBookings() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(100L, 1L));
        assertThat(exception.getMessage()).isEqualTo("User with id - 100 not found");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findByBooker(any(),any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByBookerAndEndBefore(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByBookerAndStartAfter(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findAllWithCurrentState(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByBookerAndStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponseDto> responseDtoList = bookingService
                .getAllUserBookings(1L, RequestStateEnum.ALL, 0, 5);
        assertThat(responseDtoList).isEqualTo(List.of(bookingResponseDto));
        responseDtoList = bookingService.getAllUserBookings(1L, RequestStateEnum.PAST, 0, 5);
        assertThat(responseDtoList).isEqualTo(List.of(bookingResponseDto));
        responseDtoList = bookingService.getAllUserBookings(1L, RequestStateEnum.FUTURE, 0, 5);
        assertThat(responseDtoList).isEqualTo(List.of(bookingResponseDto));
        responseDtoList = bookingService.getAllUserBookings(1L, RequestStateEnum.CURRENT, 0, 5);
        assertThat(responseDtoList).isEqualTo(List.of(bookingResponseDto));
        booking.setStatus(BookingStateEnum.WAITING);
        bookingResponseDto.setStatus(BookingStateEnum.WAITING);
        responseDtoList = bookingService.getAllUserBookings(1L, RequestStateEnum.WAITING, 0, 5);
        assertThat(responseDtoList).isEqualTo(List.of(bookingResponseDto));
        booking.setStatus(BookingStateEnum.REJECTED);
        bookingResponseDto.setStatus(BookingStateEnum.REJECTED);
        responseDtoList = bookingService.getAllUserBookings(1L, RequestStateEnum.REJECTED, 0, 5);
        assertThat(responseDtoList).isEqualTo(List.of(bookingResponseDto));
    }

    @Test
    void getAllItemsBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getAllItemsBooking(100L, RequestStateEnum.ALL, 0, 5));
        assertThat(exception.getMessage()).isEqualTo("User with id - 100 not found");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.countByOwner(any()))
                .thenReturn(0L);

        List<BookingResponseDto> responseDto = bookingService
                .getAllItemsBooking(1L, RequestStateEnum.ALL, 0, 5);
        assertThat(responseDto).isEqualTo(Collections.emptyList());

        when(itemRepository.countByOwner(any()))
                .thenReturn(1L);
        when(bookingRepository.findByItem_Owner(any(),any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByItem_OwnerAndEndBefore(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByItem_OwnerAndStartAfter(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findAllItemsBookingWithCurrentState(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByItem_OwnerAndStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponseDto> responseDtoList = bookingService
                .getAllItemsBooking(1L, RequestStateEnum.ALL, 0, 5);
        assertThat(responseDtoList).isEqualTo(List.of(bookingResponseDto));
        responseDtoList = bookingService.getAllItemsBooking(1L, RequestStateEnum.PAST, 0, 5);
        assertThat(responseDtoList).isEqualTo(List.of(bookingResponseDto));
        responseDtoList = bookingService.getAllItemsBooking(1L, RequestStateEnum.FUTURE, 0, 5);
        assertThat(responseDtoList).isEqualTo(List.of(bookingResponseDto));
        responseDtoList = bookingService.getAllItemsBooking(1L, RequestStateEnum.CURRENT, 0, 5);
        assertThat(responseDtoList).isEqualTo(List.of(bookingResponseDto));
        booking.setStatus(BookingStateEnum.WAITING);
        bookingResponseDto.setStatus(BookingStateEnum.WAITING);
        responseDtoList = bookingService.getAllItemsBooking(1L, RequestStateEnum.WAITING, 0, 5);
        assertThat(responseDtoList).isEqualTo(List.of(bookingResponseDto));
        booking.setStatus(BookingStateEnum.REJECTED);
        bookingResponseDto.setStatus(BookingStateEnum.REJECTED);
        responseDtoList = bookingService.getAllItemsBooking(1L, RequestStateEnum.REJECTED, 0, 5);
        assertThat(responseDtoList).isEqualTo(List.of(bookingResponseDto));
    }
}