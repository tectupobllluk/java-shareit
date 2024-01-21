package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.enums.RequestStateEnum;

import java.util.List;

public interface BookingService {
    BookingDtoResponse saveBooking(Long ownerId, BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse considerBooking(Long ownerId, Boolean approved, Long bookingId);

    BookingDtoResponse getBooking(Long userId, Long bookingId);

    List<BookingDtoResponse> getAllUserBookings(Long userId, RequestStateEnum state);

    List<BookingDtoResponse> getAllItemsBooking(Long userId, RequestStateEnum state);
}
