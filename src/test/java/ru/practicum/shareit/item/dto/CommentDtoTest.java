package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    @SneakyThrows
    void convertCommentDtoToJsonValuesAsExpected() {
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("Comment text")
                .itemId(1L)
                .authorName("Ivan")
                .created(LocalDateTime.now())
                .build();

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(dto.getText());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(dto.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.created").isNotNull();
    }
}
