package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description");
    private final ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto(1L,
            "description", List.of(new ItemRequestResponseDto.Item(1L, "name",
            "description", true, 1L)), LocalDateTime.now());
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.saveItem(anyLong(), any())).thenReturn(itemRequestResponseDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestResponseDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestResponseDto.getDescription()))
                .andExpect(jsonPath("$.items[0].id").value(1L))
                .andExpect(jsonPath("$.items[0].name").value("name"))
                .andExpect(jsonPath("$.items[0].description").value("description"))
                .andExpect(jsonPath("$.items[0].available").value(true))
                .andExpect(jsonPath("$.items[0].requestId").value(1L))
                .andExpect(jsonPath("$.created").value(itemRequestResponseDto.getCreated()
                        .format(formatter)));
    }

    @Test
    void getAllOwnerRequests() throws Exception {
        when(itemRequestService.getAllOwnerRequests(anyLong())).thenReturn(List.of(itemRequestResponseDto));

        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestResponseDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestResponseDto.getDescription()))
                .andExpect(jsonPath("$[0].items[0].id").value(1L))
                .andExpect(jsonPath("$[0].items[0].name").value("name"))
                .andExpect(jsonPath("$[0].items[0].description").value("description"))
                .andExpect(jsonPath("$[0].items[0].available").value(true))
                .andExpect(jsonPath("$[0].items[0].requestId").value(1L))
                .andExpect(jsonPath("$[0].created").value(itemRequestResponseDto.getCreated()
                        .format(formatter)));
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestResponseDto));

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestResponseDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestResponseDto.getDescription()))
                .andExpect(jsonPath("$[0].items[0].id").value(1L))
                .andExpect(jsonPath("$[0].items[0].name").value("name"))
                .andExpect(jsonPath("$[0].items[0].description").value("description"))
                .andExpect(jsonPath("$[0].items[0].available").value(true))
                .andExpect(jsonPath("$[0].items[0].requestId").value(1L))
                .andExpect(jsonPath("$[0].created").value(itemRequestResponseDto.getCreated()
                        .format(formatter)));
    }

    @Test
    void getRequest() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(itemRequestResponseDto);

        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestResponseDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestResponseDto.getDescription()))
                .andExpect(jsonPath("$.items[0].id").value(1L))
                .andExpect(jsonPath("$.items[0].name").value("name"))
                .andExpect(jsonPath("$.items[0].description").value("description"))
                .andExpect(jsonPath("$.items[0].available").value(true))
                .andExpect(jsonPath("$.items[0].requestId").value(1L))
                .andExpect(jsonPath("$.created").value(itemRequestResponseDto.getCreated()
                        .format(formatter)));
    }
}