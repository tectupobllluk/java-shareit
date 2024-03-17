package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.enums.BookingStateEnum;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    private final CommentDto commentDto = new CommentDto(1L, "text", "authorName",
            LocalDateTime.now());
    private final ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L);
    private final ItemResponseDto itemResponseDto = new ItemResponseDto(1L, "name", "description",
            true, new ItemResponseDto.Booking(1L, LocalDateTime.now().minusHours(1), LocalDateTime.now()
            .plusHours(1), 2L, BookingStateEnum.APPROVED, LocalDateTime.now().minusHours(2)),
            new ItemResponseDto.Booking(2L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2),
                    3L, BookingStateEnum.WAITING, LocalDateTime.now().minusHours(1)),
            List.of(commentDto), 11L);
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Test
    void createItem() throws Exception {
        when(itemService.saveItem(anyLong(), any())).thenReturn(itemResponseDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(itemResponseDto.getName()))
                .andExpect(jsonPath("$.description").value(itemResponseDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemResponseDto.getAvailable()))
                .andExpect(jsonPath("$.lastBooking.id").value(itemResponseDto.getLastBooking().getId()))
                .andExpect(jsonPath("$.lastBooking.start").value(itemResponseDto.getLastBooking().getStart()
                        .format(formatter)))
                .andExpect(jsonPath("$.lastBooking.end").value(itemResponseDto.getLastBooking().getEnd()
                        .format(formatter)))
                .andExpect(jsonPath("$.lastBooking.bookerId")
                        .value(itemResponseDto.getLastBooking().getBookerId()))
                .andExpect(jsonPath("$.lastBooking.status")
                        .value(itemResponseDto.getLastBooking().getStatus().toString()))
                .andExpect(jsonPath("$.lastBooking.creationTime")
                        .value(itemResponseDto.getLastBooking().getCreationTime().format(formatter)))
                .andExpect(jsonPath("$.nextBooking.id").value(itemResponseDto.getNextBooking().getId()))
                .andExpect(jsonPath("$.nextBooking.start").value(itemResponseDto.getNextBooking().getStart()
                        .format(formatter)))
                .andExpect(jsonPath("$.nextBooking.end").value(itemResponseDto.getNextBooking().getEnd()
                        .format(formatter)))
                .andExpect(jsonPath("$.nextBooking.bookerId")
                        .value(itemResponseDto.getNextBooking().getBookerId()))
                .andExpect(jsonPath("$.nextBooking.status")
                        .value(itemResponseDto.getNextBooking().getStatus().toString()))
                .andExpect(jsonPath("$.nextBooking.creationTime")
                        .value(itemResponseDto.getNextBooking().getCreationTime().format(formatter)))
                .andExpect(jsonPath("$.comments[0].id").value(itemResponseDto.getComments().get(0).getId()))
                .andExpect(jsonPath("$.comments[0].text")
                        .value(itemResponseDto.getComments().get(0).getText()))
                .andExpect(jsonPath("$.comments[0].authorName")
                        .value(itemResponseDto.getComments().get(0).getAuthorName()))
                .andExpect(jsonPath("$.comments[0].created")
                        .value(itemResponseDto.getComments().get(0).getCreated().format(formatter)))
                .andExpect(jsonPath("$.requestId").value(itemResponseDto.getRequestId()));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), any(), anyLong())).thenReturn(itemResponseDto);

        mockMvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(itemResponseDto.getName()))
                .andExpect(jsonPath("$.description").value(itemResponseDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemResponseDto.getAvailable()))
                .andExpect(jsonPath("$.lastBooking.id").value(itemResponseDto.getLastBooking().getId()))
                .andExpect(jsonPath("$.lastBooking.start").value(itemResponseDto.getLastBooking().getStart()
                        .format(formatter)))
                .andExpect(jsonPath("$.lastBooking.end").value(itemResponseDto.getLastBooking().getEnd()
                        .format(formatter)))
                .andExpect(jsonPath("$.lastBooking.bookerId")
                        .value(itemResponseDto.getLastBooking().getBookerId()))
                .andExpect(jsonPath("$.lastBooking.status")
                        .value(itemResponseDto.getLastBooking().getStatus().toString()))
                .andExpect(jsonPath("$.lastBooking.creationTime")
                        .value(itemResponseDto.getLastBooking().getCreationTime().format(formatter)))
                .andExpect(jsonPath("$.nextBooking.id").value(itemResponseDto.getNextBooking().getId()))
                .andExpect(jsonPath("$.nextBooking.start").value(itemResponseDto.getNextBooking().getStart()
                        .format(formatter)))
                .andExpect(jsonPath("$.nextBooking.end").value(itemResponseDto.getNextBooking().getEnd()
                        .format(formatter)))
                .andExpect(jsonPath("$.nextBooking.bookerId")
                        .value(itemResponseDto.getNextBooking().getBookerId()))
                .andExpect(jsonPath("$.nextBooking.status")
                        .value(itemResponseDto.getNextBooking().getStatus().toString()))
                .andExpect(jsonPath("$.nextBooking.creationTime")
                        .value(itemResponseDto.getNextBooking().getCreationTime().format(formatter)))
                .andExpect(jsonPath("$.comments[0].id").value(itemResponseDto.getComments().get(0).getId()))
                .andExpect(jsonPath("$.comments[0].text")
                        .value(itemResponseDto.getComments().get(0).getText()))
                .andExpect(jsonPath("$.comments[0].authorName")
                        .value(itemResponseDto.getComments().get(0).getAuthorName()))
                .andExpect(jsonPath("$.comments[0].created")
                        .value(itemResponseDto.getComments().get(0).getCreated().format(formatter)))
                .andExpect(jsonPath("$.requestId").value(itemResponseDto.getRequestId()));
    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemResponseDto);

        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(itemResponseDto.getName()))
                .andExpect(jsonPath("$.description").value(itemResponseDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemResponseDto.getAvailable()))
                .andExpect(jsonPath("$.lastBooking.id").value(itemResponseDto.getLastBooking().getId()))
                .andExpect(jsonPath("$.lastBooking.start").value(itemResponseDto.getLastBooking().getStart()
                        .format(formatter)))
                .andExpect(jsonPath("$.lastBooking.end").value(itemResponseDto.getLastBooking().getEnd()
                        .format(formatter)))
                .andExpect(jsonPath("$.lastBooking.bookerId")
                        .value(itemResponseDto.getLastBooking().getBookerId()))
                .andExpect(jsonPath("$.lastBooking.status")
                        .value(itemResponseDto.getLastBooking().getStatus().toString()))
                .andExpect(jsonPath("$.lastBooking.creationTime")
                        .value(itemResponseDto.getLastBooking().getCreationTime().format(formatter)))
                .andExpect(jsonPath("$.nextBooking.id").value(itemResponseDto.getNextBooking().getId()))
                .andExpect(jsonPath("$.nextBooking.start").value(itemResponseDto.getNextBooking().getStart()
                        .format(formatter)))
                .andExpect(jsonPath("$.nextBooking.end").value(itemResponseDto.getNextBooking().getEnd()
                        .format(formatter)))
                .andExpect(jsonPath("$.nextBooking.bookerId")
                        .value(itemResponseDto.getNextBooking().getBookerId()))
                .andExpect(jsonPath("$.nextBooking.status")
                        .value(itemResponseDto.getNextBooking().getStatus().toString()))
                .andExpect(jsonPath("$.nextBooking.creationTime")
                        .value(itemResponseDto.getNextBooking().getCreationTime().format(formatter)))
                .andExpect(jsonPath("$.comments[0].id").value(itemResponseDto.getComments().get(0).getId()))
                .andExpect(jsonPath("$.comments[0].text")
                        .value(itemResponseDto.getComments().get(0).getText()))
                .andExpect(jsonPath("$.comments[0].authorName")
                        .value(itemResponseDto.getComments().get(0).getAuthorName()))
                .andExpect(jsonPath("$.comments[0].created")
                        .value(itemResponseDto.getComments().get(0).getCreated().format(formatter)))
                .andExpect(jsonPath("$.requestId").value(itemResponseDto.getRequestId()));
    }

    @Test
    void getAllOwnerItems() throws Exception {
        when(itemService.getAllOwnerItems(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemResponseDto));

        mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemResponseDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemResponseDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemResponseDto.getAvailable()))
                .andExpect(jsonPath("$[0].lastBooking.id").value(itemResponseDto.getLastBooking().getId()))
                .andExpect(jsonPath("$[0].lastBooking.start").value(itemResponseDto.getLastBooking().getStart()
                        .format(formatter)))
                .andExpect(jsonPath("$[0].lastBooking.end").value(itemResponseDto.getLastBooking().getEnd()
                        .format(formatter)))
                .andExpect(jsonPath("$[0].lastBooking.bookerId")
                        .value(itemResponseDto.getLastBooking().getBookerId()))
                .andExpect(jsonPath("$[0].lastBooking.status")
                        .value(itemResponseDto.getLastBooking().getStatus().toString()))
                .andExpect(jsonPath("$[0].lastBooking.creationTime")
                        .value(itemResponseDto.getLastBooking().getCreationTime().format(formatter)))
                .andExpect(jsonPath("$[0].nextBooking.id").value(itemResponseDto.getNextBooking().getId()))
                .andExpect(jsonPath("$[0].nextBooking.start").value(itemResponseDto.getNextBooking().getStart()
                        .format(formatter)))
                .andExpect(jsonPath("$[0].nextBooking.end").value(itemResponseDto.getNextBooking().getEnd()
                        .format(formatter)))
                .andExpect(jsonPath("$[0].nextBooking.bookerId")
                        .value(itemResponseDto.getNextBooking().getBookerId()))
                .andExpect(jsonPath("$[0].nextBooking.status")
                        .value(itemResponseDto.getNextBooking().getStatus().toString()))
                .andExpect(jsonPath("$[0].nextBooking.creationTime")
                        .value(itemResponseDto.getNextBooking().getCreationTime().format(formatter)))
                .andExpect(jsonPath("$[0].comments[0].id").value(itemResponseDto.getComments().get(0).getId()))
                .andExpect(jsonPath("$[0].comments[0].text")
                        .value(itemResponseDto.getComments().get(0).getText()))
                .andExpect(jsonPath("$[0].comments[0].authorName")
                        .value(itemResponseDto.getComments().get(0).getAuthorName()))
                .andExpect(jsonPath("$[0].comments[0].created")
                        .value(itemResponseDto.getComments().get(0).getCreated().format(formatter)))
                .andExpect(jsonPath("$[0].requestId").value(itemResponseDto.getRequestId()));
    }

    @Test
    void searchItem() throws Exception {
        when(itemService.searchItem(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(itemResponseDto));

        mockMvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemResponseDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemResponseDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemResponseDto.getAvailable()))
                .andExpect(jsonPath("$[0].lastBooking.id").value(itemResponseDto.getLastBooking().getId()))
                .andExpect(jsonPath("$[0].lastBooking.start").value(itemResponseDto.getLastBooking().getStart()
                        .format(formatter)))
                .andExpect(jsonPath("$[0].lastBooking.end").value(itemResponseDto.getLastBooking().getEnd()
                        .format(formatter)))
                .andExpect(jsonPath("$[0].lastBooking.bookerId")
                        .value(itemResponseDto.getLastBooking().getBookerId()))
                .andExpect(jsonPath("$[0].lastBooking.status")
                        .value(itemResponseDto.getLastBooking().getStatus().toString()))
                .andExpect(jsonPath("$[0].lastBooking.creationTime")
                        .value(itemResponseDto.getLastBooking().getCreationTime().format(formatter)))
                .andExpect(jsonPath("$[0].nextBooking.id").value(itemResponseDto.getNextBooking().getId()))
                .andExpect(jsonPath("$[0].nextBooking.start").value(itemResponseDto.getNextBooking().getStart()
                        .format(formatter)))
                .andExpect(jsonPath("$[0].nextBooking.end").value(itemResponseDto.getNextBooking().getEnd()
                        .format(formatter)))
                .andExpect(jsonPath("$[0].nextBooking.bookerId")
                        .value(itemResponseDto.getNextBooking().getBookerId()))
                .andExpect(jsonPath("$[0].nextBooking.status")
                        .value(itemResponseDto.getNextBooking().getStatus().toString()))
                .andExpect(jsonPath("$[0].nextBooking.creationTime")
                        .value(itemResponseDto.getNextBooking().getCreationTime().format(formatter)))
                .andExpect(jsonPath("$[0].comments[0].id").value(itemResponseDto.getComments().get(0).getId()))
                .andExpect(jsonPath("$[0].comments[0].text")
                        .value(itemResponseDto.getComments().get(0).getText()))
                .andExpect(jsonPath("$[0].comments[0].authorName")
                        .value(itemResponseDto.getComments().get(0).getAuthorName()))
                .andExpect(jsonPath("$[0].comments[0].created")
                        .value(itemResponseDto.getComments().get(0).getCreated().format(formatter)))
                .andExpect(jsonPath("$[0].requestId").value(itemResponseDto.getRequestId()));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.created").value(commentDto.getCreated().format(formatter)));
    }
}