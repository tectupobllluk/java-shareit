package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.enums.RequestStateEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long ownerId,
                                                @RequestBody @Valid BookingRequestDto bookingRequestDto) {
        log.info("Create booking " + ownerId + ": {} - Started!", bookingRequestDto);
        return bookingClient.saveBooking(ownerId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> considerBooking(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long ownerId,
                                                  @RequestParam @NotNull Boolean approved,
                                                  @PathVariable Long bookingId) {
        log.info("Consider approve booking: {} - Started!", approved);
        return bookingClient.considerBooking(ownerId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking: {} - Started!", bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId,
                                                     @RequestParam(defaultValue = "ALL") RequestStateEnum state,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all user bookings: {} - Started!", userId);
        return bookingClient.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllItemsBooking(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId,
                                                     @RequestParam(defaultValue = "ALL") RequestStateEnum state,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all user items booking: {} and {} - Started!", userId, state);
        return bookingClient.getAllItemsBooking(userId, state, from, size);
    }
}
