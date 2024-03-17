package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.BookingStateEnum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    private final BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now()
            .plusMinutes(10), LocalDateTime.now().plusHours(1));
    private final BookingResponseDto bookingResponseDto = new BookingResponseDto(1L, LocalDateTime.now(),
            LocalDateTime.now().plusHours(1), new BookingResponseDto.Item(1L, "name", "description",
            true), new BookingResponseDto.User(1L, "name", "email"), BookingStateEnum.APPROVED);
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Test
    void createBooking() throws Exception {
        when(bookingService.saveBooking(anyLong(), any())).thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingResponseDto.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(bookingResponseDto.getEnd().format(formatter)))
                .andExpect(jsonPath("$.item.id").value(bookingResponseDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingResponseDto.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingResponseDto.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingResponseDto.getItem().getAvailable()))
                .andExpect(jsonPath("$.booker.id").value(bookingResponseDto.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingResponseDto.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(bookingResponseDto.getBooker().getEmail()))
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus().toString()));
    }

    @Test
    void considerBooking() throws Exception {
        when(bookingService.considerBooking(anyLong(), anyBoolean(), anyLong())).thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingResponseDto.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(bookingResponseDto.getEnd().format(formatter)))
                .andExpect(jsonPath("$.item.id").value(bookingResponseDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingResponseDto.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingResponseDto.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingResponseDto.getItem().getAvailable()))
                .andExpect(jsonPath("$.booker.id").value(bookingResponseDto.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingResponseDto.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(bookingResponseDto.getBooker().getEmail()))
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus().toString()));
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingResponseDto.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(bookingResponseDto.getEnd().format(formatter)))
                .andExpect(jsonPath("$.item.id").value(bookingResponseDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingResponseDto.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingResponseDto.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingResponseDto.getItem().getAvailable()))
                .andExpect(jsonPath("$.booker.id").value(bookingResponseDto.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingResponseDto.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(bookingResponseDto.getBooker().getEmail()))
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus().toString()));
    }

    @Test
    void getAllUserBookings() throws Exception {
        when(bookingService.getAllUserBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingResponseDto.getStart().format(formatter)))
                .andExpect(jsonPath("$[0].end").value(bookingResponseDto.getEnd().format(formatter)))
                .andExpect(jsonPath("$[0].item.id").value(bookingResponseDto.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(bookingResponseDto.getItem().getName()))
                .andExpect(jsonPath("$[0].item.description").value(bookingResponseDto.getItem().getDescription()))
                .andExpect(jsonPath("$[0].item.available").value(bookingResponseDto.getItem().getAvailable()))
                .andExpect(jsonPath("$[0].booker.id").value(bookingResponseDto.getBooker().getId()))
                .andExpect(jsonPath("$[0].booker.name").value(bookingResponseDto.getBooker().getName()))
                .andExpect(jsonPath("$[0].booker.email").value(bookingResponseDto.getBooker().getEmail()))
                .andExpect(jsonPath("$[0].status").value(bookingResponseDto.getStatus().toString()));
    }

    @Test
    void getAllItemsBooking() throws Exception {
        when(bookingService.getAllItemsBooking(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingResponseDto.getStart().format(formatter)))
                .andExpect(jsonPath("$[0].end").value(bookingResponseDto.getEnd().format(formatter)))
                .andExpect(jsonPath("$[0].item.id").value(bookingResponseDto.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(bookingResponseDto.getItem().getName()))
                .andExpect(jsonPath("$[0].item.description").value(bookingResponseDto.getItem().getDescription()))
                .andExpect(jsonPath("$[0].item.available").value(bookingResponseDto.getItem().getAvailable()))
                .andExpect(jsonPath("$[0].booker.id").value(bookingResponseDto.getBooker().getId()))
                .andExpect(jsonPath("$[0].booker.name").value(bookingResponseDto.getBooker().getName()))
                .andExpect(jsonPath("$[0].booker.email").value(bookingResponseDto.getBooker().getEmail()))
                .andExpect(jsonPath("$[0].status").value(bookingResponseDto.getStatus().toString()));
    }
}