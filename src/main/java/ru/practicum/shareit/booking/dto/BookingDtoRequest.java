package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDtoRequest {
    private Long itemId;
    @NotNull
    @FutureOrPresent(message = "Booking start time must be in future or present")
    private LocalDateTime start;
    @NotNull
    @Future(message = "Booking end time must be in future")
    private LocalDateTime end;
}
