package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.enums.BookingStateEnum;
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
    private Item item;
    private User booker;
    private BookingStateEnum status;

    @AllArgsConstructor
    @Data
    public static class Item {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
    }

    @AllArgsConstructor
    @Data
    public static class User {
        private Long id;
        private String name;
        private String email;
    }
}
