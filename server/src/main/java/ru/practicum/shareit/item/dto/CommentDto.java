package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
