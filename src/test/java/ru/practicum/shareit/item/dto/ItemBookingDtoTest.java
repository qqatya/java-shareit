package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemBookingDtoTest {

    @Autowired
    private JacksonTester<ItemBookingDto> json;

    @Test
    @SneakyThrows
    void testCommentDto() {
        ItemBookingDto dto = ItemBookingDto.builder()
                .id(1L)
                .name("Name")
                .description("Desc")
                .available(true)
                .lastBooking(BookingInfoDto.builder().id(1L).build())
                .nextBooking(BookingInfoDto.builder().id(2L).build())
                .comments(List.of(CommentDto.builder().id(1L).build(), CommentDto.builder().id(2L).build()))
                .build();

        JsonContent<ItemBookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(dto.getAvailable());
        assertThat(result).extractingJsonPathValue("$.lastBooking").isNotNull();
        assertThat(result).extractingJsonPathValue("$.nextBooking").isNotNull();
        assertThat(result).extractingJsonPathArrayValue("$.comments").size().isEqualTo(2);
    }
}
