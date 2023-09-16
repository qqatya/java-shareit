package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    ItemRequestDto requestDto = ItemRequestDto.builder()
            .description("Some description")
            .build();

    @Test
    @SneakyThrows
    public void createItemRequest() {
        ItemRequestDto expected = ItemRequestDto.builder()
                .id(1L)
                .description(requestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
        Mockito.when(itemRequestService.createItemRequest(1L, requestDto)).thenReturn(expected);

        String response = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, ItemRequestDto.class));
    }

    @Test
    @SneakyThrows
    public void getItemRequestsOfCurrentUser() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description(requestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
        List<ItemRequestDto> expected = List.of(dto, dto, dto);
        Mockito.when(itemRequestService.getItemRequestsByUserId(1L)).thenReturn(expected);

        String response = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, new TypeReference<List<ItemRequestDto>>() {
        }));
    }

    @Test
    @SneakyThrows
    public void getItemRequestsOfOtherUsers() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description(requestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
        List<ItemRequestDto> expected = List.of(dto, dto, dto);
        Mockito.when(itemRequestService.getItemRequestsOfOtherUsers(1L, 3, 0))
                .thenReturn(expected);

        String response = mockMvc.perform(get("/requests/all?page=0&size=3")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, new TypeReference<List<ItemRequestDto>>() {
        }));
    }

    @Test
    @SneakyThrows
    public void getItemRequestById() {
        ItemRequestDto expected = ItemRequestDto.builder()
                .id(1L)
                .description(requestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
        Mockito.when(itemRequestService.getItemRequestById(1L, 1L)).thenReturn(expected);

        String response = mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, ItemRequestDto.class));
    }
}
