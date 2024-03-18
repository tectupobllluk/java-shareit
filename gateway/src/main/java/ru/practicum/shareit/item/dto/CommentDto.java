package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}
