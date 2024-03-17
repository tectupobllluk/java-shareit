package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStateEnum;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBooker(User user, Pageable page);

    Page<Booking> findByBookerAndEndBefore(User user, LocalDateTime current, Pageable page);

    Page<Booking> findByBookerAndStartAfter(User user, LocalDateTime current, Pageable page);

    @Query("select b from Booking b " +
            "where b.booker = ?1 " +
            "and (?2 between b.start and b.end " +
            "or ?2 = b.start)")
    Page<Booking> findAllWithCurrentState(User user, LocalDateTime current, Pageable page);

    Page<Booking> findByItem_Owner(User owner, Pageable page);

    Page<Booking> findByItem_OwnerAndEndBefore(User owner, LocalDateTime current, Pageable page);

    Page<Booking> findByItem_OwnerAndStartAfter(User user, LocalDateTime current, Pageable page);

    @Query("select b from Booking b " +
            "where b.item.owner = ?1 " +
            "and ?2 between b.start and b.end")
    Page<Booking> findAllItemsBookingWithCurrentState(User user, LocalDateTime current, Pageable page);

    List<Booking> findByItem_Id(Long itemId, Sort sort);

    Page<Booking> findByBookerAndStatus(User user, BookingStateEnum waiting, Pageable page);

    Page<Booking> findByItem_OwnerAndStatus(User user, BookingStateEnum waiting, Pageable page);
}
