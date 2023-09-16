package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    @SneakyThrows
    void testItemDto() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Description")
                .created(LocalDateTime.now())
                .items(List.of(ItemDto.builder()
                        .id(1L)
                        .name("Name")
                        .description("Description")
                        .available(true)
                        .requestId(1L)
                        .build()))
                .build();

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isNotNull();
        assertThat(result).extractingJsonPathArrayValue("$.items").size().isEqualTo(1);
    }
}
