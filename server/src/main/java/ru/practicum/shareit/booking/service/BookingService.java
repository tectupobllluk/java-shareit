package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.enums.RequestStateEnum;

import java.util.List;

public interface BookingService {
    BookingResponseDto saveBooking(Long ownerId, BookingRequestDto bookingRequestDto);

    BookingResponseDto considerBooking(Long ownerId, Boolean approved, Long bookingId);

    BookingResponseDto getBooking(Long userId, Long bookingId);

    List<BookingResponseDto> getAllUserBookings(Long userId, RequestStateEnum state, Integer from, Integer size);

    List<BookingResponseDto> getAllItemsBooking(Long userId, RequestStateEnum state, Integer from, Integer size);
}
