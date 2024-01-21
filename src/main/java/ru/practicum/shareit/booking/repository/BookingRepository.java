package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStateEnum;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker(User user, Sort sort);

    List<Booking> findByBookerAndEndBefore(User user, LocalDateTime current, Sort sort);

    List<Booking> findByBookerAndStartAfter(User user, LocalDateTime current, Sort sort);

    @Query("select b from Booking b " +
            "where b.booker = ?1 " +
            "and (?2 between b.start and b.end " +
            "or ?2 = b.start)")
    List<Booking> findAllWithCurrentState(User user, LocalDateTime current, Sort sort);

    List<Booking> findByItem_Owner(User owner, Sort sort);

    List<Booking> findByItem_OwnerAndEndBefore(User owner, LocalDateTime current, Sort sort);

    List<Booking> findByItem_OwnerAndStartAfter(User user, LocalDateTime current, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.owner = ?1 " +
            "and ?2 between b.start and b.end")
    List<Booking> findAllItemsBookingWithCurrentState(User user, LocalDateTime current, Sort sort);

    List<Booking> findByItem_Id(Long itemId, Sort sort);

    List<Booking> findByBookerAndStatus(User user, BookingStateEnum waiting, Sort sort);

    List<Booking> findByItem_OwnerAndStatus(User user, BookingStateEnum waiting, Sort sort);
}
