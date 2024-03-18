package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.BookingStateEnum;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class ItemServiceTest {

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private CommentRepository commentRepository;

    @Autowired
    private ItemService itemService;

    private final User owner = new User(1L, "name", "email@email.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "itemRequest", owner, LocalDateTime.now());
    private final Item item = new Item(1L, "itemName", "itemDescription",
            true, owner, itemRequest);
    private final ItemDto itemDto = new ItemDto(1L, "itemName",
            "itemDescription", true, 1L);
    private final ItemResponseDto itemResponseDto = new ItemResponseDto(1L, "itemName",
            "itemDescription", true, null, null,
            Collections.emptyList(), 1L);
    private final Booking lastBooking = new Booking(1L, LocalDateTime.now().minusHours(1)
            .truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS), item,
            owner, BookingStateEnum.APPROVED, LocalDateTime.now().minusHours(2).truncatedTo(ChronoUnit.SECONDS));
    private final Booking nextBooking = new Booking(2L, LocalDateTime.now().plusHours(2)
            .truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS),
            item, owner, BookingStateEnum.WAITING, LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS));
    private final Comment comment = new Comment(1L, "text", item, owner,
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    private final CommentDto commentDto = new CommentDto(1L, "text",
            "name", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    private final ItemResponseDto itemResponseDtoWithObjects = new ItemResponseDto(1L, "itemName",
            "itemDescription", true, new BookingDto(1L, LocalDateTime.now().minusHours(1)
            .truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS),
            1L, BookingStateEnum.APPROVED, LocalDateTime.now().minusHours(2).truncatedTo(ChronoUnit.SECONDS)),
            new BookingDto(2L, LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.SECONDS),
                    LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS), 1L,
                    BookingStateEnum.WAITING, LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS)),
            List.of(commentDto), 1L);

    @Test
    void saveItem() {
        when(userRepository.findById(100L))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.saveItem(100L, itemDto));
        assertThat(exception.getMessage()).isEqualTo("Owner with id 100 is not created!");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemResponseDto newItemResponseDto = itemService.saveItem(1L, itemDto);

        assertThat(newItemResponseDto).isEqualTo(itemResponseDto);
        assertThat(newItemResponseDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "itemName")
                .hasFieldOrPropertyWithValue("description", "itemDescription")
                .hasFieldOrPropertyWithValue("available", true)
                .hasFieldOrPropertyWithValue("lastBooking", null)
                .hasFieldOrPropertyWithValue("nextBooking", null)
                .hasFieldOrPropertyWithValue("comments", Collections.emptyList())
                .hasFieldOrPropertyWithValue("requestId", 1L);
    }

    @Test
    void updateItem() {
        when(userRepository.findById(100L))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(100L, itemDto, 1L));
        assertThat(exception.getMessage()).isEqualTo("Owner with id 100 is not created!");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(100L))
                .thenReturn(Optional.empty());

        final NotFoundException itemException = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, itemDto, 100L));
        assertThat(itemException.getMessage()).isEqualTo("Item with id 100 is not created!");

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        final BadRequestException badRequestException = assertThrows(BadRequestException.class,
                () -> itemService.updateItem(2L, itemDto, 1L));
        assertThat(badRequestException.getMessage()).isEqualTo("Item owner and request owner not equals!");

        when(itemRepository.save(any()))
                .thenReturn(item);
        when(commentRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        ItemResponseDto responseDto = itemService.updateItem(1L, itemDto, 1L);
        assertThat(responseDto).isEqualTo(itemResponseDto);
        assertThat(responseDto.getLastBooking()).isNull();
        assertThat(responseDto.getNextBooking()).isNull();
        assertThat(responseDto.getComments()).isEqualTo(Collections.emptyList());

        when(bookingRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(List.of(lastBooking, nextBooking));
        when(commentRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(List.of(comment));

        ItemResponseDto newResponseDto = itemService.updateItem(1L, itemDto, 1L);
        assertThat(newResponseDto).isEqualTo(itemResponseDtoWithObjects);
        assertThat(newResponseDto.getComments()).isEqualTo(List.of(commentDto));
    }

    @Test
    void getItem() {
        when(itemRepository.findById(100L))
                .thenReturn(Optional.empty());

        final NotFoundException itemException = assertThrows(NotFoundException.class,
                () -> itemService.getItem(1L, 100L));
        assertThat(itemException.getMessage()).isEqualTo("Item with id 100 not found!");

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        ItemResponseDto responseDto = itemService.getItem(1L, 1L);
        assertThat(responseDto).isEqualTo(itemResponseDto);
        assertThat(responseDto.getLastBooking()).isNull();
        assertThat(responseDto.getNextBooking()).isNull();
        assertThat(responseDto.getComments()).isEqualTo(Collections.emptyList());

        when(bookingRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(List.of(lastBooking, nextBooking));
        when(commentRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(List.of(comment));

        ItemResponseDto newResponseDto = itemService.getItem(1L, 1L);
        assertThat(newResponseDto).isEqualTo(itemResponseDtoWithObjects);
        assertThat(newResponseDto.getLastBooking()).isEqualTo(itemResponseDtoWithObjects.getLastBooking());
        assertThat(newResponseDto.getNextBooking()).isEqualTo(itemResponseDtoWithObjects.getNextBooking());
        assertThat(newResponseDto.getComments()).isEqualTo(List.of(commentDto));
    }

    @Test
    void getAllOwnerItems() {
        final BadRequestException badRequestException = assertThrows(BadRequestException.class,
                () -> itemService.getAllOwnerItems(null, 0, 5));
        assertThat(badRequestException.getMessage()).isEqualTo("Owner can't be empty!");

        when(itemRepository.findByOwner_Id(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(commentRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        List<ItemResponseDto> responseDto = itemService.getAllOwnerItems(1L, 0, 5);
        assertThat(responseDto).isEqualTo(List.of(itemResponseDto));
        assertThat(responseDto.get(0).getLastBooking()).isNull();
        assertThat(responseDto.get(0).getNextBooking()).isNull();
        assertThat(responseDto.get(0).getComments()).isEqualTo(Collections.emptyList());

        when(bookingRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(List.of(lastBooking, nextBooking));
        when(commentRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(List.of(comment));

        List<ItemResponseDto> newResponseDto = itemService.getAllOwnerItems(1L, 0, 5);
        assertThat(newResponseDto).isEqualTo(List.of(itemResponseDtoWithObjects));
        assertThat(newResponseDto.get(0).getLastBooking()).isEqualTo(itemResponseDtoWithObjects.getLastBooking());
        assertThat(newResponseDto.get(0).getNextBooking()).isEqualTo(itemResponseDtoWithObjects.getNextBooking());
        assertThat(newResponseDto.get(0).getComments()).isEqualTo(List.of(commentDto));
    }

    @Test
    void searchItem() {
        assertThat(itemService.searchItem(1L, "", 0, 5)).isEqualTo(Collections.emptyList());

        when(itemRepository.search(anyString(), any()))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(commentRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        List<ItemResponseDto> responseDto = itemService.searchItem(1L, "text", 0, 5);
        assertThat(responseDto).isEqualTo(List.of(itemResponseDto));
        assertThat(responseDto.get(0).getLastBooking()).isNull();
        assertThat(responseDto.get(0).getNextBooking()).isNull();
        assertThat(responseDto.get(0).getComments()).isEqualTo(Collections.emptyList());

        when(commentRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(List.of(comment));

        List<ItemResponseDto> newResponseDto = itemService.searchItem(1L, "text", 0, 5);
        assertThat(newResponseDto.get(0).getId()).isEqualTo(itemResponseDtoWithObjects.getId());
        assertThat(newResponseDto.get(0).getName()).isEqualTo(itemResponseDtoWithObjects.getName());
        assertThat(newResponseDto.get(0).getDescription()).isEqualTo(itemResponseDtoWithObjects.getDescription());
        assertThat(newResponseDto.get(0).getAvailable()).isEqualTo(itemResponseDtoWithObjects.getAvailable());
        assertThat(newResponseDto.get(0).getLastBooking()).isNull();
        assertThat(newResponseDto.get(0).getNextBooking()).isNull();
        assertThat(newResponseDto.get(0).getComments()).isEqualTo(itemResponseDtoWithObjects.getComments());
        assertThat(newResponseDto.get(0).getRequestId()).isEqualTo(itemResponseDtoWithObjects.getRequestId());
    }

    @Test
    void addComment() {
        when(userRepository.findById(100L))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(100L, 1L, commentDto));
        assertThat(exception.getMessage()).isEqualTo("Owner with id 100 is not created!");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(100L))
                .thenReturn(Optional.empty());

        final NotFoundException itemException = assertThrows(NotFoundException.class,
                () -> itemService.addComment(1L, 100L, commentDto));
        assertThat(itemException.getMessage()).isEqualTo("Item with id 100 is not created!");

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        final BadRequestException badRequestException = assertThrows(BadRequestException.class,
                () -> itemService.addComment(1L, 1L, commentDto));
        assertThat(badRequestException.getMessage())
                .isEqualTo("Commentator do not have item bookings or booking is not finished");

        lastBooking.setEnd(LocalDateTime.now().minusMinutes(30));
        when(bookingRepository.findByItem_Id(anyLong(), any()))
                .thenReturn(List.of(lastBooking));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto newCommentDto = itemService.addComment(1L, 1L, commentDto);
        assertThat(newCommentDto).isEqualTo(commentDto);
    }
}