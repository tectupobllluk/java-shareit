package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.enums.BookingStateEnum;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User booker;
    @Enumerated(EnumType.STRING)
    private BookingStateEnum status;
    @Column(name = "creation_time")
    private LocalDateTime creationTime;
}
