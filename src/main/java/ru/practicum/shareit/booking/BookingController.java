package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.RequestStateEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long ownerId,
                                            @RequestBody @Valid BookingDtoRequest bookingDtoRequest) {
        log.info("Create booking " + ownerId + ": {} - Started!", bookingDtoRequest);
        BookingDtoResponse bookingDtoResponse = bookingService.saveBooking(ownerId, bookingDtoRequest);
        log.info("Create booking: {} - Finished!", bookingDtoResponse);
        return bookingDtoResponse;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse considerBooking(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long ownerId,
                                              @RequestParam @NotNull Boolean approved, @PathVariable Long bookingId) {
        log.info("Consider approve booking: {} - Started!", approved);
        BookingDtoResponse bookingDtoResponse = bookingService.considerBooking(ownerId, approved, bookingId);
        log.info("Consider approve booking: {} - Finished!", bookingDtoResponse);
        return bookingDtoResponse;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId,
                                         @PathVariable Long bookingId) {
        log.info("Get booking: {} - Started!", bookingId);
        BookingDtoResponse bookingDtoResponse = bookingService.getBooking(userId, bookingId);
        log.info("Get booking: {} - Finished!", bookingDtoResponse);
        return bookingDtoResponse;
    }

    @GetMapping
    public List<BookingDtoResponse> getAllUserBookings(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId,
                                                       @RequestParam(defaultValue = "ALL") RequestStateEnum state) {
        log.info("Get all user bookings: {} - Started!", userId);
        List<BookingDtoResponse> bookingDtoResponse = bookingService.getAllUserBookings(userId, state);
        log.info("Get all user bookings: {} - Finished!", bookingDtoResponse);
        return bookingDtoResponse;
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllItemsBooking(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId,
                                                        @RequestParam(defaultValue = "ALL") RequestStateEnum state) {
        log.info("Get all user items booking: {} and {} - Started!", userId, state);
        List<BookingDtoResponse> bookingDtoResponse = bookingService.getAllItemsBooking(userId, state);
        log.info("Get all user items booking: {} - Finished!", bookingDtoResponse);
        return bookingDtoResponse;
    }
}
