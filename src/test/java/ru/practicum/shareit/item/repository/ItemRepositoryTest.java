package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
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
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void search() {
        User owner = new User(1L, "first user", "first@first.ru");
        userRepository.save(owner);
        ItemRequest itemRequest = new ItemRequest(1L, "itemRequest", owner, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        Item availableName = new Item(1L, "nameBoatname", "description", true,
                owner, 1L);
        itemRepository.save(availableName);
        Item availableDescription = new Item(2L, "name", "descriptionboatdescription", true,
                owner, 1L);
        itemRepository.save(availableDescription);
        Item availableNoText = new Item(3L, "namename", "description", true,
                owner, 1L);
        itemRepository.save(availableNoText);
        Item notAvailable = new Item(4L, "boatname", "descriptionboat", false,
                owner, 1L);
        itemRepository.save(notAvailable);

        assertThat(itemRepository.search("unknown text", PageRequest.of(0, 5))
                .getTotalElements()).isEqualTo(0);
        assertThat(itemRepository.search("BOAT", PageRequest.of(0, 5))
                .getTotalElements()).isEqualTo(2);
        assertThat(itemRepository.search("Boat", PageRequest.of(0, 5)).getContent()
                .get(0)).isEqualTo(availableName);
        assertThat(itemRepository.search("bOAT", PageRequest.of(0, 5)).getContent()
                .get(1)).isEqualTo(availableDescription);
        assertThat(itemRepository.search("boat", PageRequest.of(0, 5)).getContent())
                .doesNotContain(availableNoText, notAvailable);
    }
}