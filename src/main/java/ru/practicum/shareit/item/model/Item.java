package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items")
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @ToString.Exclude
    private User owner;
//    @ElementCollection
//    @CollectionTable(name = "requests", joinColumns = @JoinColumn(name = "id"))
//    @Column(name = "id")
    private Long requestId;
}


