package ru.practicum.shareit.user.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String name;
    private String email;
}