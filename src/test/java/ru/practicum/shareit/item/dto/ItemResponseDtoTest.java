package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.enums.BookingStateEnum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemResponseDtoTest {

    @Autowired
    private JacksonTester<ItemResponseDto> json;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Test
    void testItemResponseDto() throws Exception {
        LocalDateTime firstStart = LocalDateTime.now().plusHours(1L);
        LocalDateTime firstEnd = LocalDateTime.now().plusHours(2L);
        LocalDateTime firstCreation = LocalDateTime.now();
        BookingDto lastBooking = new BookingDto(1L, firstStart,
                firstEnd, 6L, BookingStateEnum.APPROVED, firstCreation);
        LocalDateTime secondStart = LocalDateTime.now().plusHours(3L);
        LocalDateTime secondEnd = LocalDateTime.now().plusHours(4L);
        LocalDateTime secondCreation = LocalDateTime.now().plusSeconds(10L);
        BookingDto nextBooking = new BookingDto(2L, secondStart,
                secondEnd, 7L, BookingStateEnum.WAITING, secondCreation);
        CommentDto firstComment = new CommentDto(3L, "first text",
                "first author name", firstCreation);
        CommentDto secondComment = new CommentDto(4L, "second text",
                "second author name", secondCreation);
        ItemResponseDto itemResponseDto = new ItemResponseDto(5L, "item", "description",
                true, lastBooking, nextBooking, List.of(firstComment, secondComment), 8L);

        JsonContent<ItemResponseDto> result = json.write(itemResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo(firstStart.format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end")
                .isEqualTo(firstEnd.format(formatter));
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(6);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.creationTime")
                .isEqualTo(firstCreation.format(formatter));
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start")
                .isEqualTo(secondStart.format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end")
                .isEqualTo(secondEnd.format(formatter));
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(7);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.creationTime")
                .isEqualTo(secondCreation.format(formatter));
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("first text");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo("first author name");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo(firstCreation.format(formatter));
        assertThat(result).extractingJsonPathNumberValue("$.comments[1].id").isEqualTo(4);
        assertThat(result).extractingJsonPathStringValue("$.comments[1].text")
                .isEqualTo("second text");
        assertThat(result).extractingJsonPathStringValue("$.comments[1].authorName")
                .isEqualTo("second author name");
        assertThat(result).extractingJsonPathStringValue("$.comments[1].created")
                .isEqualTo(secondCreation.format(formatter));
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(8);
    }
}