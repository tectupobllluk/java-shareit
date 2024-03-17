package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStateEnum;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void findAllWithCurrentState() {
        User owner = new User(1L, "first user", "first@first.ru");
        userRepository.save(owner);
        User notOwner = new User(2L, "notOwner", "second@second.ru");
        userRepository.save(notOwner);
        ItemRequest itemRequest = new ItemRequest(1L, "itemRequest", owner, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        Item item = new Item(1L, "nameBoatname", "description", true,
                owner, itemRequest);
        itemRepository.save(item);

        Booking before = new Booking(1L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), item,
                owner, BookingStateEnum.APPROVED, LocalDateTime.now().minusHours(3));
        bookingRepository.save(before);
        Booking after = new Booking(2L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), item,
                owner, BookingStateEnum.APPROVED, LocalDateTime.now());
        bookingRepository.save(after);
        Booking current = new Booking(3L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), item,
                owner, BookingStateEnum.APPROVED, LocalDateTime.now().minusHours(3));
        bookingRepository.save(current);
        Booking startCurrent = new Booking(4L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item,
                owner, BookingStateEnum.APPROVED, LocalDateTime.now().minusHours(3));
        bookingRepository.save(startCurrent);
        Booking currentNotOwner = new Booking(5L, LocalDateTime.now().minusHours(2),
                LocalDateTime.now().plusHours(1), item, notOwner, BookingStateEnum.APPROVED,
                LocalDateTime.now().minusHours(3));
        bookingRepository.save(currentNotOwner);

        assertThat(bookingRepository.findAllWithCurrentState(notOwner, LocalDateTime.now(),
                        PageRequest.of(0, 5)).getTotalElements()).isEqualTo(1);
        assertThat(bookingRepository.findAllWithCurrentState(notOwner, LocalDateTime.now(),
                        PageRequest.of(0, 5)).getContent().get(0)).isEqualTo(currentNotOwner);
        assertThat(bookingRepository.findAllWithCurrentState(owner, LocalDateTime.now(), PageRequest.of(0, 5))
                .getTotalElements()).isEqualTo(2);
        assertThat(bookingRepository.findAllWithCurrentState(owner, LocalDateTime.now(), PageRequest.of(0, 5))
                .getContent().get(0)).isEqualTo(current);
        assertThat(bookingRepository.findAllWithCurrentState(owner, LocalDateTime.now(), PageRequest.of(0, 5))
                .getContent().get(1)).isEqualTo(startCurrent);
        assertThat(bookingRepository.findAllWithCurrentState(owner, LocalDateTime.now(), PageRequest.of(0, 5))
                .getContent()).doesNotContain(before, after, currentNotOwner);
    }

    @Test
    void findAllItemsBookingWithCurrentState() {
        User owner = new User(1L, "first user", "first@first.ru");
        userRepository.save(owner);
        User notOwner = new User(2L, "notOwner", "second@second.ru");
        userRepository.save(notOwner);
        ItemRequest itemRequest = new ItemRequest(1L, "itemRequest", owner, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        Item item = new Item(1L, "nameBoatname", "description", true,
                owner, itemRequest);
        itemRepository.save(item);
        ItemRequest secondItemRequest = new ItemRequest(2L, "secondItemRequest", owner, LocalDateTime.now());
        itemRequestRepository.save(secondItemRequest);
        Item secondItem = new Item(2L, "secondName", "description", true,
                owner, secondItemRequest);
        itemRepository.save(secondItem);

        Booking before = new Booking(1L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), item,
                owner, BookingStateEnum.APPROVED, LocalDateTime.now().minusHours(3));
        bookingRepository.save(before);
        Booking after = new Booking(2L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), item,
                owner, BookingStateEnum.APPROVED, LocalDateTime.now());
        bookingRepository.save(after);
        Booking current = new Booking(3L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), item,
                owner, BookingStateEnum.APPROVED, LocalDateTime.now().minusHours(3));
        bookingRepository.save(current);
        Booking currentNotOwner = new Booking(4L, LocalDateTime.now().minusHours(2),
                LocalDateTime.now().plusHours(1), item, notOwner, BookingStateEnum.APPROVED,
                LocalDateTime.now().minusHours(3));
        bookingRepository.save(currentNotOwner);
        Booking currentSecondItem = new Booking(5L, LocalDateTime.now().minusHours(2),
                LocalDateTime.now().plusHours(5), secondItem, notOwner, BookingStateEnum.APPROVED,
                LocalDateTime.now().minusHours(3));
        bookingRepository.save(currentSecondItem);

        assertThat(bookingRepository.findAllItemsBookingWithCurrentState(notOwner, LocalDateTime.now(),
                PageRequest.of(0, 5)).getTotalElements()).isEqualTo(0);
        assertThat(bookingRepository.findAllItemsBookingWithCurrentState(owner, LocalDateTime.now(),
                        PageRequest.of(0, 5)).getTotalElements()).isEqualTo(3);
        assertThat(bookingRepository.findAllItemsBookingWithCurrentState(owner, LocalDateTime.now(),
                        PageRequest.of(0, 5)).getContent().get(0)).isEqualTo(current);
        assertThat(bookingRepository.findAllItemsBookingWithCurrentState(owner, LocalDateTime.now(),
                        PageRequest.of(0, 5)).getContent().get(1)).isEqualTo(currentNotOwner);
        assertThat(bookingRepository.findAllItemsBookingWithCurrentState(owner, LocalDateTime.now(),
                PageRequest.of(0, 5)).getContent().get(2)).isEqualTo(currentSecondItem);
        assertThat(bookingRepository.findAllItemsBookingWithCurrentState(owner, LocalDateTime.now(),
                        PageRequest.of(0, 5)).getContent()).doesNotContain(before, after);
    }
}