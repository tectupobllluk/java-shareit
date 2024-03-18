package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

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
    private List<ItemDto> items;
    private LocalDateTime created;
}
