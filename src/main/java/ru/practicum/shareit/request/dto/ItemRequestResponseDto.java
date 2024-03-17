package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ItemRequestResponseDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotEmpty
    private String description;
    private List<Item> items;
    private LocalDateTime created;

    @AllArgsConstructor
    @Data
    public static class Item {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;
    }
}
