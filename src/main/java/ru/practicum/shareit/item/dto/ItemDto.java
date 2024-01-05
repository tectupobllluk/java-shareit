package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ItemDto {
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
}
