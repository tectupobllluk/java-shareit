package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class ItemRequestServiceTest {

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestService itemRequestService;

    private final User owner = new User(1L, "name", "email@email.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "description", owner,
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    private final Item item = new Item(1L, "itemName", "itemDescription",
            true, owner, itemRequest);
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description");
    private final ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto(1L,
            "description", Collections.emptyList(), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    private final ItemRequestResponseDto itemResponseDtoWithItem = new ItemRequestResponseDto(1L,
            "description", List.of(new ItemDto(1L, "itemName",
            "itemDescription", true, 1L)),
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

    @Test
    void saveItem() {
        when(userRepository.findById(100L))
                .thenReturn(Optional.empty());
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.saveItem(100L, itemRequestDto));
        assertThat(exception.getMessage()).isEqualTo("User with id - 100 not found");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));

        ItemRequestResponseDto newItemResponseDto = itemRequestService.saveItem(1L, itemRequestDto);

        assertThat(newItemResponseDto).isEqualTo(itemRequestResponseDto);
        assertThat(newItemResponseDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", "description")
                .hasFieldOrPropertyWithValue("items", Collections.emptyList())
                .hasFieldOrPropertyWithValue("created", itemRequestResponseDto.getCreated());
    }

    @Test
    void getAllOwnerRequests() {
        when(userRepository.findById(100L))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllOwnerRequests(100L));
        assertThat(exception.getMessage()).isEqualTo("User with id - 100 not found");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRequestRepository.findByRequester_Id(anyLong(), any()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(Collections.emptyList());

        List<ItemRequestResponseDto> responseDtoList = itemRequestService.getAllOwnerRequests(1L);
        assertThat(responseDtoList).isEqualTo(List.of(itemRequestResponseDto));
        assertThat(responseDtoList.get(0).getItems()).isEqualTo(Collections.emptyList());

        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(List.of(item));

        List<ItemRequestResponseDto> newResponseDtoList = itemRequestService.getAllOwnerRequests(1L);
        assertThat(newResponseDtoList).isEqualTo(List.of(itemResponseDtoWithItem));
        assertThat(newResponseDtoList.get(0).getItems()).isEqualTo(itemResponseDtoWithItem.getItems());
    }

    @Test
    void getAllRequests() {
        when(userRepository.findById(100L))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllRequests(100L, 0, 5));
        assertThat(exception.getMessage()).isEqualTo("User with id - 100 not found");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRequestRepository.findOtherRequests(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(Collections.emptyList());

        List<ItemRequestResponseDto> responseDtoList = itemRequestService.getAllRequests(1L, 0, 5);
        assertThat(responseDtoList).isEqualTo(List.of(itemRequestResponseDto));
        assertThat(responseDtoList.get(0).getItems()).isEqualTo(Collections.emptyList());

        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(List.of(item));

        List<ItemRequestResponseDto> newResponseDtoList = itemRequestService.getAllRequests(1L, 0, 5);
        assertThat(newResponseDtoList).isEqualTo(List.of(itemResponseDtoWithItem));
        assertThat(newResponseDtoList.get(0).getItems()).isEqualTo(itemResponseDtoWithItem.getItems());
    }

    @Test
    void getRequestById() {
        when(userRepository.findById(100L))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(100L, 1L));
        assertThat(exception.getMessage()).isEqualTo("User with id - 100 not found");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(100L))
                .thenReturn(Optional.empty());

        final NotFoundException itemException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(1L, 100L));
        assertThat(itemException.getMessage()).isEqualTo("Request with id - 100 not found");

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(Collections.emptyList());

        ItemRequestResponseDto responseDtoList = itemRequestService.getRequestById(1L, 1L);
        assertThat(responseDtoList).isEqualTo(itemRequestResponseDto);
        assertThat(responseDtoList.getItems()).isEqualTo(Collections.emptyList());

        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(List.of(item));

        ItemRequestResponseDto responseDtoListWithItem = itemRequestService.getRequestById(1L, 1L);
        assertThat(responseDtoListWithItem).isEqualTo(itemResponseDtoWithItem);
        assertThat(responseDtoListWithItem.getItems()).isEqualTo(itemResponseDtoWithItem.getItems());
    }
}