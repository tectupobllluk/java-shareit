package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStateEnum;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerOrderByCreationTimeDesc(User user);

    List<Booking> findByBookerAndEndBeforeOrderByCreationTimeDesc(User user, LocalDateTime current);

    List<Booking> findByBookerAndStartAfterOrderByCreationTimeDesc(User user, LocalDateTime current);

    @Query("select b from Booking b " +
            "where b.booker = ?1 " +
            "and (?2 between b.start and b.end " +
            "or ?2 = b.start) " +
            "order by b.creationTime asc")
    List<Booking> findAllWithCurrentState(User user, LocalDateTime current);

    List<Booking> findByItem_OwnerOrderByCreationTimeDesc(User owner);

    List<Booking> findByItem_OwnerAndEndBeforeOrderByCreationTimeDesc(User owner, LocalDateTime current);

    List<Booking> findByItem_OwnerAndStartAfterOrderByCreationTimeDesc(User user, LocalDateTime current);

    @Query("select b from Booking b " +
            "where b.item.owner = ?1 " +
            "and ?2 between b.start and b.end " +
            "order by b.creationTime asc")
    List<Booking> findAllItemsBookingWithCurrentState(User user, LocalDateTime current);

    List<Booking> findByItem_IdOrderByStartAsc(Long itemId);

    List<Booking> findByBookerAndStatusOrderByCreationTimeDesc(User user, BookingStateEnum waiting);

    List<Booking> findByItem_OwnerAndStatusOrderByCreationTimeDesc(User user, BookingStateEnum waiting);
}
