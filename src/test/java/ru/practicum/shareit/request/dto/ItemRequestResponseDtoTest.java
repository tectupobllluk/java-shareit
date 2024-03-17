package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestResponseDtoTest {

    @Autowired
    private JacksonTester<ItemRequestResponseDto> json;

    @Test
    void testItemRequestResponseDto() throws Exception {
        ItemRequestResponseDto.Item firstItem = new ItemRequestResponseDto.Item(1L, "first name",
                "first description", true, 1L);
        ItemRequestResponseDto.Item secondItem = new ItemRequestResponseDto.Item(2L, "second name",
                "second description", true, 2L);
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto(1L, "item",
                List.of(firstItem, secondItem), dateTime);

        JsonContent<ItemRequestResponseDto> result = json.write(itemRequestResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("first name");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo("first description");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[1].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.items[1].name").isEqualTo("second name");
        assertThat(result).extractingJsonPathStringValue("$.items[1].description")
                .isEqualTo("second description");
        assertThat(result).extractingJsonPathBooleanValue("$.items[1].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items[1].requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(dateTime.format(DateTimeFormatter.ISO_DATE_TIME));
    }
}