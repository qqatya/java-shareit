package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentMapperTest {

    private final CommentMapper commentMapper;

    private final User author = User.builder()
            .id(1L)
            .name("Ivan")
            .email("ivan@test.com")
            .build();
    private final User owner = User.builder()
            .id(2L)
            .name("Arkadiy")
            .email("arkadiy@test.com")
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .owner(owner)
            .name("Camera")
            .description("Analog camera")
            .available(true)
            .build();
    private final Comment comment = Comment.builder()
            .id(1L)
            .text("Some text")
            .item(item)
            .author(author)
            .createDttm(LocalDateTime.now())
            .build();

    private final CommentDto commentDto = CommentDto.builder()
            .id(comment.getId())
            .text(comment.getText())
            .authorName(comment.getAuthor().getName())
            .created(comment.getCreateDttm())
            .itemId(comment.getItem().getId())
            .build();

    @Test
    public void testMappingToDto() {
        CommentDto dto = commentMapper.mapToDto(comment);

        assertEquals(commentDto, dto);
    }

    @Test
    public void testMappingToModel() {
        Comment actual = commentMapper.mapToModel(commentDto, item, author);

        assertEquals(comment.getText(), actual.getText());
        assertNotNull(actual.getCreateDttm());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
    }
}
