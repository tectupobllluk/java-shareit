package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@Builder
public class User {
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String email;
}
