package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    @SneakyThrows
    void convertBookingDtoToJsonValuesAsExpected() {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .build();

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotNull();
    }
}
