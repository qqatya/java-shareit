package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.practicum.shareit.booking.model.type.BookingStatus.APPROVED;

@JsonTest
public class BookingInfoDtoTest {

    @Autowired
    private JacksonTester<BookingInfoDto> json;

    @Test
    @SneakyThrows
    void convertBookingInfoDtoToJsonValuesAsExpected() {
        BookingInfoDto dto = BookingInfoDto.builder()
                .id(1L)
                .bookerId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(APPROVED)
                .build();

        JsonContent<BookingInfoDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(dto.getStatus().toString());
    }
}
