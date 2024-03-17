package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotNull
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @ToString.Exclude
    private User requester;
    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;
}
