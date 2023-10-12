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
import static ru.practicum.shareit.util.Header.SHARER_USER_ID;

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
    public void createItemRequestStatusCodeIsOkAndResponseAsExpected() {
        ItemRequestDto expected = ItemRequestDto.builder()
                .id(1L)
                .description(requestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
        Mockito.when(itemRequestService.createItemRequest(1L, requestDto)).thenReturn(expected);

        String response = mockMvc.perform(post("/requests")
                        .header(SHARER_USER_ID, 1)
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
    public void getItemRequestsOfCurrentUserStatusCodeIsOkAndResponseAsExpected() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description(requestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
        List<ItemRequestDto> expected = List.of(dto, dto, dto);
        Mockito.when(itemRequestService.getItemRequestsByUserId(1L)).thenReturn(expected);

        String response = mockMvc.perform(get("/requests")
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, new TypeReference<List<ItemRequestDto>>() {
        }));
    }

    @Test
    @SneakyThrows
    public void getItemRequestsOfOtherUsersStatusCodeIsOkAndResponseAsExpected() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description(requestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
        List<ItemRequestDto> expected = List.of(dto, dto, dto);
        Mockito.when(itemRequestService.getItemRequestsOfOtherUsers(1L, 3, 0))
                .thenReturn(expected);

        String response = mockMvc.perform(get("/requests/all?from=0&size=3")
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, new TypeReference<List<ItemRequestDto>>() {
        }));
    }

    @Test
    @SneakyThrows
    public void getItemRequestByIdStatusCodeIsOkAndResponseAsExpected() {
        ItemRequestDto expected = ItemRequestDto.builder()
                .id(1L)
                .description(requestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
        Mockito.when(itemRequestService.getItemRequestById(1L, 1L)).thenReturn(expected);

        String response = mockMvc.perform(get("/requests/1")
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, objectMapper.readValue(response, ItemRequestDto.class));
    }
}
