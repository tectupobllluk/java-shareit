package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.enums.BookingStateEnum;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BookingResponseDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    private BookingStateEnum status;
}
