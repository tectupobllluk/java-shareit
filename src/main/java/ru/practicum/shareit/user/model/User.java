package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@Builder
public class User {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String name;
    private String email;
}
