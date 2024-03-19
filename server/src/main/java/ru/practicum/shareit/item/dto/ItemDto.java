package ru.practicum.shareit.item.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ItemDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
