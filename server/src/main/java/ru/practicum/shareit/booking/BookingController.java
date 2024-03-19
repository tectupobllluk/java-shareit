package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.RequestStateEnum;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                            @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Create booking " + ownerId + ": {} - Started!", bookingRequestDto);
        BookingResponseDto bookingResponseDto = bookingService.saveBooking(ownerId, bookingRequestDto);
        log.info("Create booking: {} - Finished!", bookingResponseDto);
        return bookingResponseDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto considerBooking(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                              @RequestParam Boolean approved,
                                              @PathVariable Long bookingId) {
        log.info("Consider approve booking: {} - Started!", approved);
        BookingResponseDto bookingResponseDto = bookingService.considerBooking(ownerId, approved, bookingId);
        log.info("Consider approve booking: {} - Finished!", bookingResponseDto);
        return bookingResponseDto;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId) {
        log.info("Get booking: {} - Started!", bookingId);
        BookingResponseDto bookingResponseDto = bookingService.getBooking(userId, bookingId);
        log.info("Get booking: {} - Finished!", bookingResponseDto);
        return bookingResponseDto;
    }

    @GetMapping
    public List<BookingResponseDto> getAllUserBookings(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "ALL") RequestStateEnum state,
                                                       @RequestParam(defaultValue = "0") Integer from,
                                                       @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all user bookings: {} - Started!", userId);
        List<BookingResponseDto> bookingResponseDto = bookingService.getAllUserBookings(userId, state, from, size);
        log.info("Get all user bookings: {} - Finished!", bookingResponseDto);
        return bookingResponseDto;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllItemsBooking(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "ALL") RequestStateEnum state,
                                                       @RequestParam(defaultValue = "0") Integer from,
                                                       @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all user items booking: {} and {} - Started!", userId, state);
        List<BookingResponseDto> bookingResponseDto = bookingService.getAllItemsBooking(userId, state, from, size);
        log.info("Get all user items booking: {} - Finished!", bookingResponseDto);
        return bookingResponseDto;
    }
}
