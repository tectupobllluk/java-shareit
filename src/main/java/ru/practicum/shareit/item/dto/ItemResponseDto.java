package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.enums.BookingStateEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentDto> comments = new ArrayList<>();

    @AllArgsConstructor
    @Data
    public static class Booking {
        private Long id;
        private LocalDateTime start;
        private LocalDateTime end;
        private Long bookerId;
        private BookingStateEnum status;
        private LocalDateTime creationTime;
    }
}
