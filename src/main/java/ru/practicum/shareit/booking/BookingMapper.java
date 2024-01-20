package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStateEnum;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class BookingMapper {

    public static Booking fromBookingDtoRequest(User booker, Item item, BookingDtoRequest bookingDto) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStateEnum.WAITING)
                .creationTime(LocalDateTime.now())
                .build();
    }

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        Item item = booking.getItem();
        User user = booking.getBooker();
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(new BookingDtoResponse.Item(
                        item.getId(), item.getName(), item.getDescription(), item.getAvailable()))
                .booker(new BookingDtoResponse.User(user.getId(), user.getName(), user.getEmail()))
                .status(booking.getStatus())
                .build();
    }
}
