package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.enums.BookingStateEnum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingResponseDtoTest {

    @Autowired
    private JacksonTester<BookingResponseDto> json;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Test
    void testBookingResponseDto() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusHours(1L);
        LocalDateTime end = LocalDateTime.now().plusHours(2L);
        BookingResponseDto.Item item = new BookingResponseDto.Item(1L, "item name",
                "item description", false);
        BookingResponseDto.User booker = new BookingResponseDto.User(2L, "user name", "email@email.ru");
        BookingResponseDto bookingResponseDto = new BookingResponseDto(3L, start, end, item, booker,
                BookingStateEnum.REJECTED);

        JsonContent<BookingResponseDto> result = json.write(bookingResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(formatter));
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item name");
        assertThat(result).extractingJsonPathStringValue("$.item.description")
                .isEqualTo("item description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(false);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("user name");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("email@email.ru");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("REJECTED");
    }
}