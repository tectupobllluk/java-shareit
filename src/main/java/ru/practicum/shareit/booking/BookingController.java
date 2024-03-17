package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.RequestStateEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long ownerId,
                                            @RequestBody @Valid BookingRequestDto bookingRequestDto) {
        log.info("Create booking " + ownerId + ": {} - Started!", bookingRequestDto);
        BookingResponseDto bookingResponseDto = bookingService.saveBooking(ownerId, bookingRequestDto);
        log.info("Create booking: {} - Finished!", bookingResponseDto);
        return bookingResponseDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto considerBooking(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long ownerId,
                                              @RequestParam @NotNull Boolean approved, @PathVariable Long bookingId) {
        log.info("Consider approve booking: {} - Started!", approved);
        BookingResponseDto bookingResponseDto = bookingService.considerBooking(ownerId, approved, bookingId);
        log.info("Consider approve booking: {} - Finished!", bookingResponseDto);
        return bookingResponseDto;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId,
                                         @PathVariable Long bookingId) {
        log.info("Get booking: {} - Started!", bookingId);
        BookingResponseDto bookingResponseDto = bookingService.getBooking(userId, bookingId);
        log.info("Get booking: {} - Finished!", bookingResponseDto);
        return bookingResponseDto;
    }

    @GetMapping
    public List<BookingResponseDto> getAllUserBookings(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId,
                                                       @RequestParam(defaultValue = "ALL") RequestStateEnum state,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all user bookings: {} - Started!", userId);
        List<BookingResponseDto> bookingResponseDto = bookingService.getAllUserBookings(userId, state, from, size);
        log.info("Get all user bookings: {} - Finished!", bookingResponseDto);
        return bookingResponseDto;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllItemsBooking(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId,
                                                       @RequestParam(defaultValue = "ALL") RequestStateEnum state,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all user items booking: {} and {} - Started!", userId, state);
        List<BookingResponseDto> bookingResponseDto = bookingService.getAllItemsBooking(userId, state, from, size);
        log.info("Get all user items booking: {} - Finished!", bookingResponseDto);
        return bookingResponseDto;
    }
}
