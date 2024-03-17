package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findOtherRequests() {
        User firstUser = new User(1L, "first user", "first@first.ru");
        userRepository.save(firstUser);
        User secondUser = new User(2L, "second user", "second@second.ru");
        userRepository.save(secondUser);
        ItemRequest firstRequest = new ItemRequest(1L, "first description", firstUser, LocalDateTime.now());
        ItemRequest secondRequest = new ItemRequest(2L, "second description", firstUser,
                LocalDateTime.now());

        itemRequestRepository.save(firstRequest);
        itemRequestRepository.save(secondRequest);

        assertThat(itemRequestRepository.findOtherRequests(1L, PageRequest.of(0, 2))
                .getTotalElements()).isEqualTo(0);
        assertThat(itemRequestRepository.findOtherRequests(2L, PageRequest.of(0, 3))
                .getTotalElements()).isEqualTo(2);
        assertThat(itemRequestRepository.findOtherRequests(2L, PageRequest.of(0, 2)).getContent()
                .get(0)).isEqualTo(firstRequest);
        assertThat(itemRequestRepository.findOtherRequests(2L, PageRequest.of(0, 2)).getContent()
                .get(1)).isEqualTo(secondRequest);
    }
}