package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private final ItemDto itemDto = ItemDto.builder()
            .name("Camera")
            .description("Analog camera")
            .available(true)
            .build();

    @Test
    @SneakyThrows
    public void createItem() {
        ItemDto expected = ItemDto.builder()
                .id(1L)
                .name("Camera")
                .description("Analog camera")
                .available(true)
                .build();
        Mockito.when(itemService.createItem(itemDto, 1L)).thenReturn(expected);

        String response = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, ItemDto.class));
    }

    @Test
    @SneakyThrows
    public void updateItem() {
        ItemDto toUpdate = itemDto;
        toUpdate.setName("Updated camera");
        toUpdate.setAvailable(false);
        ItemDto expected = ItemDto.builder()
                .id(1L)
                .name("Updated camera")
                .description("Analog camera")
                .available(false)
                .build();
        Mockito.when(itemService.updateItem(1L, 1L, itemDto)).thenReturn(expected);

        String response = mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, ItemDto.class));
    }

    @Test
    @SneakyThrows
    public void getItemById() {
        ItemBookingDto expected = ItemBookingDto.builder()
                .id(1L)
                .name("Camera")
                .description("Analog camera")
                .available(true)
                .build();
        Mockito.when(itemService.getItemById(1L, 1L))
                .thenReturn(expected);

        String response = mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, ItemBookingDto.class));
    }

    @Test
    @SneakyThrows
    public void getItems() {
        ItemBookingDto dto = ItemBookingDto.builder()
                .id(1L)
                .name("Camera")
                .description("Analog camera")
                .available(true)
                .build();
        List<ItemBookingDto> expected = List.of(dto, dto, dto);
        Mockito.when(itemService.getItems(1L, 3, 0))
                .thenReturn(expected);

        String response = mockMvc.perform(get("/items?from=0&size=3")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, new TypeReference<List<ItemBookingDto>>() {
        }));
    }

    @Test
    @SneakyThrows
    public void searchItems() {
        List<ItemDto> expected = List.of(ItemDto.builder()
                .id(1L)
                .name("Camera")
                .description("Analog camera")
                .available(true)
                .build());
        Mockito.when(itemService.searchItems("Analog", 1, 0))
                .thenReturn(expected);

        String response = mockMvc.perform(get("/items/search?text=Analog&from=0&size=1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, new TypeReference<List<ItemDto>>() {
        }));
    }

    @Test
    @SneakyThrows
    public void createComment() {
        CommentDto toCreate = CommentDto.builder()
                .text("Some text")
                .itemId(1L)
                .build();
        CommentDto expected = CommentDto.builder()
                .id(1L)
                .text("Some text")
                .itemId(1L)
                .authorName("Ivan")
                .created(LocalDateTime.now())
                .build();
        Mockito.when(itemService.createComment(toCreate, 1L, 1L))
                .thenReturn(expected);

        String response = mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, CommentDto.class));
    }


}
