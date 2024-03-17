package ru.practicum.shareit.request.dto;


import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ItemRequestDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotEmpty
    private String description;
}
