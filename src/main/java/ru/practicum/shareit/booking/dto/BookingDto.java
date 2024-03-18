package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.enums.BookingStateEnum;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BookingDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId;
    private BookingStateEnum status;
    private LocalDateTime creationTime;
}
